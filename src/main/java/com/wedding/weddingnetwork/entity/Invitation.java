package com.wedding.weddingnetwork.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "invitations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invitationId;

    private Long eventId;
    private Long personId;

    @Builder.Default
    private String channel = "WHATSAPP";

    private String personalizedCardUrl;

    @Builder.Default
    private String deliveryStatus = "QUEUED";

    @Builder.Default
    private Byte retryCount = 0;

    private String providerMessageId;

    @Builder.Default
    private String rsvpStatus = "PENDING";

    @Builder.Default
    private Byte guestCount = 1;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
        if (this.channel == null) {
            this.channel = "WHATSAPP";
        }
        if (this.deliveryStatus == null) {
            this.deliveryStatus = "QUEUED";
        }
        if (this.rsvpStatus == null) {
            this.rsvpStatus = "PENDING";
        }
        if (this.guestCount == null) {
            this.guestCount = 1;
        }
        if (this.retryCount == null) {
            this.retryCount = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
