package com.wedding.weddingnetwork.repository;

import com.wedding.weddingnetwork.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    List<Invitation> findByEventIdAndDeliveryStatus(Long eventId, String deliveryStatus);

    List<Invitation> findTop100ByEventIdAndDeliveryStatusOrderByInvitationIdAsc(Long eventId, String deliveryStatus);

    boolean existsByEventIdAndPersonId(Long eventId, Long personId);

    @Modifying
    @Transactional
    @Query(value = """
        INSERT IGNORE INTO invitations (event_id, person_id, channel, delivery_status, rsvp_status, guest_count, retry_count, updated_at)
        SELECT :eventId, p.person_id, 'WHATSAPP', 'QUEUED', 'PENDING', 1, 0, NOW()
        FROM persons p
        LEFT JOIN locations l ON p.native_location_id = l.location_id
        WHERE p.phone_number IS NOT NULL 
          AND TRIM(p.phone_number) != ''
          AND p.whatsapp_opt_in = 1
          AND (:hubId IS NULL OR p.hub_id = :hubId)
          AND (:district IS NULL OR :district = '' OR LOWER(l.district) = LOWER(:district))
          AND NOT EXISTS (
              SELECT 1 FROM invitations i WHERE i.event_id = :eventId AND i.person_id = p.person_id
          )
        """, nativeQuery = true)
    int bulkEnqueueForEvent(
            @Param("eventId") Long eventId,
            @Param("hubId") Long hubId,
            @Param("district") String district
    );

    @Query(value = """
        SELECT 
            COUNT(*) AS totalInvitations,
            COALESCE(SUM(CASE WHEN delivery_status = 'QUEUED' THEN 1 ELSE 0 END), 0) AS queued,
            COALESCE(SUM(CASE WHEN delivery_status = 'SENT' THEN 1 ELSE 0 END), 0) AS sent,
            COALESCE(SUM(CASE WHEN delivery_status = 'DELIVERED' THEN 1 ELSE 0 END), 0) AS delivered,
            COALESCE(SUM(CASE WHEN delivery_status = 'FAILED' THEN 1 ELSE 0 END), 0) AS failed,
            COALESCE(SUM(CASE WHEN rsvp_status = 'COMING' THEN guest_count ELSE 0 END), 0) AS confirmedGuests,
            COALESCE(SUM(CASE WHEN rsvp_status = 'NOT_COMING' THEN 1 ELSE 0 END), 0) AS declinedGuests,
            COALESCE(SUM(CASE WHEN rsvp_status = 'PENDING' THEN 1 ELSE 0 END), 0) AS pendingRsvp
        FROM invitations
        WHERE event_id = :eventId
        """, nativeQuery = true)
    EventInvitationStats getEventInvitationStats(@Param("eventId") Long eventId);

    interface EventInvitationStats {
        Long getTotalInvitations();
        Long getQueued();
        Long getSent();
        Long getDelivered();
        Long getFailed();
        Long getConfirmedGuests();
        Long getDeclinedGuests();
        Long getPendingRsvp();
    }
}
