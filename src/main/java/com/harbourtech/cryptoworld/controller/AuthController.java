package com.harbourtech.cryptoworld.controller;

import com.harbourtech.cryptoworld.dto.VerifySignatureRequest;
import com.harbourtech.cryptoworld.models.ApiResponseStatus;
import com.harbourtech.cryptoworld.service.AuthService;
import com.harbourtech.cryptoworld.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.SignatureException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/request-nonce")
    public ApiResponse<String> requestNonce(@RequestParam String publicAddress) {
        return authService.generateNonce(publicAddress);
    }

    @PostMapping("/verify-signature")
    public ApiResponse<Object> verifySignature(@RequestBody VerifySignatureRequest request) {
        try {
            return authService.verifySignature(
                    request.getNonce(),
                    request.getSignature(),
                    request.getPublicAddress()
            );
        } catch (RuntimeException | SignatureException e) {
            return ApiResponse.error(ApiResponseStatus.BAD_REQUEST, e.getMessage());
        }
    }


}
