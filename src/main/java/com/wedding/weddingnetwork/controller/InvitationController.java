package com.wedding.weddingnetwork.controller;

import com.wedding.weddingnetwork.dto.RsvpUpdateRequest;
import com.wedding.weddingnetwork.entity.Invitation;
import com.wedding.weddingnetwork.service.InvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/invitations")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    // 1. Bulk Enqueue for Event with optional targeting by hubId or district
    @PostMapping("/enqueue/{eventId}")
    public ResponseEntity<Map<String, Object>> enqueueAll(
            @PathVariable("eventId") Long eventId,
            @RequestParam(value = "hubId", required = false) Long hubId,
            @RequestParam(value = "district", required = false) String district) {
        return ResponseEntity.ok(invitationService.enqueueAllForEvent(eventId, hubId, district));
    }

    // 2. Async Throttled Dispatcher
    @PostMapping("/dispatch/{eventId}")
    public ResponseEntity<Map<String, String>> startDispatch(@PathVariable("eventId") Long eventId) {
        if (invitationService.isDispatchRunning(eventId)) {
            return ResponseEntity.ok(Map.of(
                    "status", "ALREADY_RUNNING",
                    "message", "Dispatch is already in progress for event " + eventId
            ));
        }

        invitationService.processQueuedInvitations(eventId);
        return ResponseEntity.ok(Map.of(
                "status", "DISPATCH_STARTED",
                "message", "Background throttled dispatch initiated for event " + eventId
        ));
    }

    // 3. Guest RSVP Update
    @PatchMapping("/rsvp/{invitationId}")
    public ResponseEntity<Invitation> updateRsvp(
            @PathVariable("invitationId") Long invitationId,
            @Valid @RequestBody RsvpUpdateRequest request) {
        return ResponseEntity.ok(invitationService.updateRsvp(invitationId, request));
    }

    // 4. Live Dashboard Stats
    @GetMapping("/stats/{eventId}")
    public ResponseEntity<Map<String, Object>> getStats(@PathVariable("eventId") Long eventId) {
        return ResponseEntity.ok(invitationService.getEventStats(eventId));
    }
}
