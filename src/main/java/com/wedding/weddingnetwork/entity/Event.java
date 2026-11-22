package com.wedding.weddingnetwork.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    private String eventTitle;
    private LocalDateTime eventDate;
    private Long venueLocationId;
    private String cardTemplateUrl;
    private LocalDateTime createdAt;
}