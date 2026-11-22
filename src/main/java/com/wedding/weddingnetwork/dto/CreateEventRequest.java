package com.wedding.weddingnetwork.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequest {
    @NotBlank(message = "Event title is required")
    private String eventTitle;

    @NotNull(message = "Event date is required")
    private LocalDateTime eventDate;

    private Long venueLocationId;

    private String cardTemplateUrl;
}

