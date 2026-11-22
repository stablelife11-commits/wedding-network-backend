package com.wedding.weddingnetwork.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMarriageRequest {
    @NotNull(message = "Husband ID is required")
    private Long husbandId;

    @NotNull(message = "Wife ID is required")
    private Long wifeId;

    private Short weddingYear;

    private Long weddingLocationId;

    private Long inLawsLocationId;

    @Pattern(regexp = "^(ACTIVE|DIVORCED|WIDOWED)$", message = "Status must be ACTIVE, DIVORCED, or WIDOWED")
    private String status;
}

