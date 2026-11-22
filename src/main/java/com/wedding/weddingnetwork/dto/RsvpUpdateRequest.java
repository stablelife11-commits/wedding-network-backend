package com.wedding.weddingnetwork.dto;

import jakarta.validation.constraints.Min;
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
public class RsvpUpdateRequest {
    @NotBlank(message = "RSVP status is required")
    @Pattern(regexp = "^(COMING|NOT_COMING|MAYBE)$", message = "Status must be COMING, NOT_COMING, or MAYBE")
    private String status;

    @Min(value = 1, message = "Guest count must be at least 1")
    private Byte guestCount;
}

