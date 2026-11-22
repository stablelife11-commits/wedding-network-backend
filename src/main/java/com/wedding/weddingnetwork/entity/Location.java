package com.wedding.weddingnetwork.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "locations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    private String placeName;
    private String villageCity;
    private String tehsil;
    private String district;

    @Builder.Default
    private String state = "Uttar Pradesh";

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    private String googlePlaceId;

    @PrePersist
    public void prePersist() {
        if (this.state == null) {
            this.state = "Uttar Pradesh";
        }
    }
}
