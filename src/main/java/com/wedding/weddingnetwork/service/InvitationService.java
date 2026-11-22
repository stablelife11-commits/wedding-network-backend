package com.wedding.weddingnetwork.service;

import com.wedding.weddingnetwork.entity.Invitation;
import com.wedding.weddingnetwork.entity.Person;
import com.wedding.weddingnetwork.repository.InvitationRepository;
import com.wedding.weddingnetwork.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final PersonRepository personRepository;

    // 1. Bulk Enqueue: Sabhi valid phone wale logo ko event ki queue mein daalna
    public Map<String, Object> enqueueAllForEvent(Long eventId) {
        List<Person> allPersons = personRepository.findAll();
        List<Invitation> toSave = new ArrayList<>();
        int skipped = 0;

        for (Person p : allPersons) {
            if (p.getPhoneNumber() == null || !Boolean.TRUE.equals(p.getWhatsappOptIn())) {
                skipped++;
                continue;
            }
            if (invitationRepository.existsByEventIdAndPersonId(eventId, p.getPersonId())) {
                skipped++;
                continue;
            }

            Invitation inv = new Invitation();
            inv.setEventId(eventId);
            inv.setPersonId(p.getPersonId());
            inv.setChannel("WHATSAPP");
            inv.setDeliveryStatus("QUEUED");
            inv.setRsvpStatus("PENDING");
            inv.setGuestCount((byte) 1);
            inv.setUpdatedAt(LocalDateTime.now());
            toSave.add(inv);
        }

        invitationRepository.saveAll(toSave);

        return Map.of(
                "eventId", eventId,
                "newlyQueued", toSave.size(),
                "skippedOrDuplicate", skipped
        );
    }

    // 2. Async Throttled Dispatcher (20,000 messages ko safe speed par bhejne ke liye)
    @Async
    public void processQueuedInvitations(Long eventId) {
        List<Invitation> queuedList = invitationRepository.findByEventIdAndDeliveryStatus(eventId, "QUEUED");

        for (Invitation inv : queuedList) {
            try {
                // Yahan real WhatsApp Cloud API call lagegi (Abhi simulate kar rahe hain)
                inv.setDeliveryStatus("SENT");
                inv.setProviderMessageId("wamid_" + UUID.randomUUID().toString().substring(0, 8));
                inv.setUpdatedAt(LocalDateTime.now());
                invitationRepository.save(inv);

                // Rate Limit: ~50 msg/sec (20ms gap) taaki WhatsApp ban na ho
                Thread.sleep(20);
            } catch (Exception e) {
                inv.setDeliveryStatus("FAILED");
                inv.setRetryCount((byte) (inv.getRetryCount() + 1));
                invitationRepository.save(inv);
            }
        }
    }

    // 3. Guest RSVP Update (Webhook ya Button Click se)
    public Invitation updateRsvp(Long invitationId, String status, Byte guestCount) {
        Invitation inv = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitation not found: " + invitationId));
        inv.setRsvpStatus(status.toUpperCase()); // COMING, NOT_COMING, MAYBE
        if (guestCount != null) inv.setGuestCount(guestCount);
        inv.setUpdatedAt(LocalDateTime.now());
        return invitationRepository.save(inv);
    }

    // 4. Live Dashboard Stats (Catering & Delivery Tracking)
    public Map<String, Object> getEventStats(Long eventId) {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("eventId", eventId);
        stats.put("queued", invitationRepository.countByEventIdAndDeliveryStatus(eventId, "QUEUED"));
        stats.put("sent", invitationRepository.countByEventIdAndDeliveryStatus(eventId, "SENT"));
        stats.put("delivered", invitationRepository.countByEventIdAndDeliveryStatus(eventId, "DELIVERED"));
        stats.put("failed", invitationRepository.countByEventIdAndDeliveryStatus(eventId, "FAILED"));
        stats.put("totalConfirmedGuests", invitationRepository.getTotalConfirmedGuests(eventId));
        return stats;
    }
}