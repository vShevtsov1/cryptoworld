package com.harbourtech.cryptoworld.service;

import com.harbourtech.cryptoworld.dto.AuthResponse;
import com.harbourtech.cryptoworld.entity.User;
import com.harbourtech.cryptoworld.models.ApiResponseStatus;
import com.harbourtech.cryptoworld.repository.UserRepository;
import com.harbourtech.cryptoworld.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Sign;

import java.security.SignatureException;
import java.util.Optional;


@Service
public class AuthService {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;

    public ApiResponse<String> generateNonce(String publicAddress) {

        if (publicAddress == null || publicAddress.isBlank()) {
            return ApiResponse.error(ApiResponseStatus.BAD_REQUEST, "Public address is required");
        }
        Optional<User> userOpt = userRepository.findByPublicAddress(publicAddress.toLowerCase());

        String nonce = UUIDGenerator.generateUUID();

        User user = userOpt.orElseGet(() -> User.builder()
                .publicAddress(publicAddress.toLowerCase())
                .build());

        user.setNonce(nonce);
        userRepository.save(user);

        return ApiResponse.success("Nonce generated successfully", nonce);
    }

    public ApiResponse<Object> verifySignature(String nonce, String signature, String publicAddress) throws SignatureException {
        User user = userRepository.findByPublicAddress(publicAddress.toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getNonce().equals(nonce)) {
            return ApiResponse.error(ApiResponseStatus.BAD_REQUEST, "Invalid nonce");
        }

        byte[] msgHash = EthereumSignatureUtil.getPersonalMessageHash(nonce);
        Sign.SignatureData sigData = EthereumSignatureUtil.extractSignature(signature);
        String recoveredAddress = EthereumSignatureUtil.recoverAddressFromSignature(msgHash, sigData, publicAddress);

        if (!recoveredAddress.equalsIgnoreCase(publicAddress)) {
            return ApiResponse.error(ApiResponseStatus.BAD_REQUEST, "Signature does not match wallet");
        }

        user.setNonce(UUIDGenerator.generateUUID());
        userRepository.save(user);

        return ApiResponse.success("Signature verified", AuthResponse.builder()
                .publicAddress(user.getPublicAddress())
                .type("Bearer")
                .expirationTime(Constants.ACCESS_TOKEN_EXPIRATION_TIME)
                .userId(user.getId().toString())
                .token(jwtUtil.generateToken(user.getId().toString(), user.getPublicAddress(), Constants.ACCESS_TOKEN_EXPIRATION_TIME)).build());
    }


}
