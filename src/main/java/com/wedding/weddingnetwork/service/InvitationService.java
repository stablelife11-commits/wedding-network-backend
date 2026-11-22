package com.wedding.weddingnetwork.service;

import com.wedding.weddingnetwork.dto.RsvpUpdateRequest;
import com.wedding.weddingnetwork.entity.Invitation;
import com.wedding.weddingnetwork.exception.ResourceNotFoundException;
import com.wedding.weddingnetwork.repository.EventRepository;
import com.wedding.weddingnetwork.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final EventRepository eventRepository;

    // In-memory active-event lock to prevent concurrent duplicate dispatches
    private final Set<Long> activeDispatches = ConcurrentHashMap.newKeySet();

    // 1. Bulk Enqueue: Native SQL bulk insert into invitations table (handles 20,000+ instantaneously)
    public Map<String, Object> enqueueAllForEvent(Long eventId, Long hubId, String district) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found with ID: " + eventId);
        }

        int newlyQueued = invitationRepository.bulkEnqueueForEvent(eventId, hubId, district);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("eventId", eventId);
        response.put("hubId", hubId);
        response.put("district", district);
        response.put("newlyQueued", newlyQueued);
        response.put("message", "Bulk enqueue operation completed successfully");
        return response;
    }

    // 2. Async Throttled Dispatcher: Process in chunks of 100 with event-level concurrency guard
    @Async("taskExecutor")
    public void processQueuedInvitations(Long eventId) {
        if (!activeDispatches.add(eventId)) {
            log.warn("Dispatch is already in progress for eventId: {}. Skipping duplicate trigger.", eventId);
            return;
        }

        log.info("Starting async invitation dispatch for eventId: {}", eventId);
        try {
            while (true) {
                // Fetch next 100 QUEUED records
                List<Invitation> chunk = invitationRepository.findTop100ByEventIdAndDeliveryStatusOrderByInvitationIdAsc(eventId, "QUEUED");
                if (chunk.isEmpty()) {
                    log.info("No more queued invitations for eventId: {}. Dispatch complete.", eventId);
                    break;
                }

                for (Invitation inv : chunk) {
                    try {
                        // Simulate WhatsApp Cloud API message send
                        inv.setDeliveryStatus("SENT");
                        inv.setProviderMessageId("wamid_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
                        inv.setUpdatedAt(LocalDateTime.now());
                        invitationRepository.save(inv);

                        // Throttled Rate Limit: ~50 msg/sec (20ms interval) to adhere to provider quotas
                        Thread.sleep(20);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Dispatch thread interrupted for eventId: {}", eventId);
                        return;
                    } catch (Exception e) {
                        log.error("Failed to dispatch invitation ID: {} - {}", inv.getInvitationId(), e.getMessage());
                        inv.setDeliveryStatus("FAILED");
                        byte currentRetries = inv.getRetryCount() != null ? inv.getRetryCount() : 0;
                        inv.setRetryCount((byte) (currentRetries + 1));
                        inv.setUpdatedAt(LocalDateTime.now());
                        invitationRepository.save(inv);
                    }
                }
            }
        } finally {
            activeDispatches.remove(eventId);
            log.info("Dispatch finished and event lock released for eventId: {}", eventId);
        }
    }

    public boolean isDispatchRunning(Long eventId) {
        return activeDispatches.contains(eventId);
    }

    // 3. Guest RSVP Update
    public Invitation updateRsvp(Long invitationId, RsvpUpdateRequest request) {
        Invitation inv = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found with ID: " + invitationId));

        inv.setRsvpStatus(request.getStatus().toUpperCase());
        if (request.getGuestCount() != null) {
            inv.setGuestCount(request.getGuestCount());
        }
        inv.setUpdatedAt(LocalDateTime.now());
        return invitationRepository.save(inv);
    }

    // 4. Live Dashboard Stats: Single optimized native SQL aggregate query
    public Map<String, Object> getEventStats(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found with ID: " + eventId);
        }

        InvitationRepository.EventInvitationStats stats = invitationRepository.getEventInvitationStats(eventId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("eventId", eventId);
        response.put("totalInvitations", stats != null ? stats.getTotalInvitations() : 0L);
        response.put("queued", stats != null ? stats.getQueued() : 0L);
        response.put("sent", stats != null ? stats.getSent() : 0L);
        response.put("delivered", stats != null ? stats.getDelivered() : 0L);
        response.put("failed", stats != null ? stats.getFailed() : 0L);
        response.put("totalConfirmedGuests", stats != null ? stats.getConfirmedGuests() : 0L);
        response.put("declinedGuests", stats != null ? stats.getDeclinedGuests() : 0L);
        response.put("pendingRsvp", stats != null ? stats.getPendingRsvp() : 0L);
        return response;
    }
}
