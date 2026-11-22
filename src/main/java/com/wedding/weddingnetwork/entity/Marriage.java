package com.wedding.weddingnetwork.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "marriages")
@Data
public class Marriage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long marriageId;

    private Long husbandId;
    private Long wifeId;
    private Short weddingYear;
    private Long weddingLocationId;
    private Long inLawsLocationId;
    private String status = "ACTIVE"; // ACTIVE, DIVORCED, WIDOWED
}