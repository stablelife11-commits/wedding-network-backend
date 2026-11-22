package com.wedding.weddingnetwork.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "marriages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Marriage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long marriageId;

    private Long husbandId;
    private Long wifeId;
    private Short weddingYear;
    private Long weddingLocationId;
    private Long inLawsLocationId;

    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, DIVORCED, WIDOWED

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = "ACTIVE";
        }
    }
}
