package com.wedding.weddingnetwork.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "invitations")
@Data
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invitationId;

    private Long eventId;
    private Long personId;
    private String channel = "WHATSAPP";
    private String personalizedCardUrl;

    private String deliveryStatus = "QUEUED";
    private Byte retryCount = 0;
    private String providerMessageId;

    private String rsvpStatus = "PENDING";
    private Byte guestCount = 1;
    private LocalDateTime updatedAt;
}