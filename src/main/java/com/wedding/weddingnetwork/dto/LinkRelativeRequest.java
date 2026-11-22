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
public class LinkRelativeRequest {
    @NotBlank(message = "Relation type is required")
    @Pattern(regexp = "^(FATHER|MOTHER|BROTHER|SISTER|SIBLING)$", message = "Relation type must be FATHER, MOTHER, BROTHER, SISTER or SIBLING")
    private String relationType;

    private String gender;

    private Long existingRelativeId;

    private String fullName;

    private Short birthYear;
}

