package com.wedding.weddingnetwork.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePersonRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Gender must be MALE, FEMALE, or OTHER")
    private String gender;

    private String phoneNumber;

    private Boolean whatsappOptIn;

    private Short birthYear;

    private Boolean isAlive;

    private Long fatherId;

    private Long motherId;

    private Long hubId;

    private Long nativeLocationId;

    private Long currentLocationId;
}

