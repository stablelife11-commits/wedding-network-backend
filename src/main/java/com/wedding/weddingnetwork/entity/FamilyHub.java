package com.wedding.weddingnetwork.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "family_hubs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyHub {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hubId;

    private String familyName;
    private String surname;
    private Long nativeLocationId;
    private Long headPersonId;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
