package com.wedding.weddingnetwork.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Long personId;
    private String fullName;
    private String phoneNumber;
    private Long hubId;
    private boolean registered;
    private String message;
}

