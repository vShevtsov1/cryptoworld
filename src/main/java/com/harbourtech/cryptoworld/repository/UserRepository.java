package com.harbourtech.cryptoworld.repository;

import com.harbourtech.cryptoworld.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByPublicAddress(String publicAddress);
    Optional<User> findByNonce(String nonce);
}
