package com.wedding.weddingnetwork.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "family_hubs")
@Data
public class FamilyHub {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hubId;

    private String familyName;
    private String surname;
    private Long nativeLocationId;
    private Long headPersonId;
    private LocalDateTime createdAt;
}