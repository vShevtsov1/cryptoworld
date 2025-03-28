package com.harbourtech.cryptoworld.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {

    private String userId;
    private String publicAddress;
    private String type;
    private Long expirationTime;
    private String token;
}
