package com.wedding.weddingnetwork.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "persons")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personId;

    private String fullName;
    private String gender; // MALE, FEMALE, OTHER
    private String phoneNumber;

    @Builder.Default
    private Boolean whatsappOptIn = true;

    private Short birthYear;

    @Builder.Default
    private Boolean isAlive = true;

    private Long fatherId;
    private Long motherId;
    private Long hubId;
    private Long nativeLocationId;
    private Long currentLocationId;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.whatsappOptIn == null) {
            this.whatsappOptIn = true;
        }
        if (this.isAlive == null) {
            this.isAlive = true;
        }
    }
}
