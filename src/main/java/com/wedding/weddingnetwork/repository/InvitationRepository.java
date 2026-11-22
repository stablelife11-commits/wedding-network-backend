package com.wedding.weddingnetwork.repository;

import com.wedding.weddingnetwork.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    List<Invitation> findByEventIdAndDeliveryStatus(Long eventId, String deliveryStatus);

    boolean existsByEventIdAndPersonId(Long eventId, Long personId);

    @Query("SELECT COALESCE(SUM(i.guestCount), 0) FROM Invitation i WHERE i.eventId = :eventId AND i.rsvpStatus = 'COMING'")
    Long getTotalConfirmedGuests(@Param("eventId") Long eventId);

    long countByEventIdAndDeliveryStatus(Long eventId, String deliveryStatus);
}