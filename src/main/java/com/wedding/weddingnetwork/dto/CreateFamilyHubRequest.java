package com.wedding.weddingnetwork.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFamilyHubRequest {
    @NotBlank(message = "Family name is required")
    private String familyName;

    private String surname;

    private Long nativeLocationId;

    private Long headPersonId;
}

