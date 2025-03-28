package com.harbourtech.cryptoworld.dto;

import lombok.Data;

@Data
public class VerifySignatureRequest {
    private String nonce;
    private String signature;
    private String publicAddress;
}
