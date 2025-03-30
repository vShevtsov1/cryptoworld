package com.harbourtech.cryptoworld.repository;

import com.harbourtech.cryptoworld.entity.Company;
import com.harbourtech.cryptoworld.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

    List<Company> getAllByUser(User user);

    Company getCompanyById(UUID id);
}
