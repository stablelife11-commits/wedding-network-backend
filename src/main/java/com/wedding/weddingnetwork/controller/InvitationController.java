package com.wedding.weddingnetwork.controller;

import com.wedding.weddingnetwork.entity.Invitation;
import com.wedding.weddingnetwork.service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/invitations")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    // 1. Sabhi logo ko Event 1 ki queue mein daalein (Browser/Postman se test karein)
    @GetMapping("/enqueue/{eventId}")
    public Map<String, Object> enqueueAll(@PathVariable("eventId") Long eventId) {
        return invitationService.enqueueAllForEvent(eventId);
    }

    // 2. Background mein messages bhejna start karein
    @GetMapping("/dispatch/{eventId}")
    public Map<String, String> startDispatch(@PathVariable("eventId") Long eventId) {
        invitationService.processQueuedInvitations(eventId);
        return Map.of("status", "Background dispatch started for event " + eventId);
    }

    // 3. Live Stats check karein
    @GetMapping("/stats/{eventId}")
    public Map<String, Object> getStats(@PathVariable("eventId") Long eventId) {
        return invitationService.getEventStats(eventId);
    }

    // 4. RSVP Update (e.g., ?status=COMING&guestCount=5)
    @GetMapping("/rsvp/{invitationId}")
    public Invitation updateRsvp(
            @PathVariable("invitationId") Long invitationId,
            @RequestParam("status") String status,
            @RequestParam(value = "guestCount", required = false) Byte guestCount) {
        return invitationService.updateRsvp(invitationId, status, guestCount);
    }
}