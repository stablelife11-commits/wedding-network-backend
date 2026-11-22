package com.wedding.weddingnetwork.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "persons")
@Data
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personId;

    private String fullName;
    private String gender; // MALE, FEMALE, OTHER
    private String phoneNumber;
    private Boolean whatsappOptIn = true;
    private Short birthYear;
    private Boolean isAlive = true;

    private Long fatherId;
    private Long motherId;
    private Long hubId;
    private Long nativeLocationId;
    private Long currentLocationId;
    private LocalDateTime createdAt;
}