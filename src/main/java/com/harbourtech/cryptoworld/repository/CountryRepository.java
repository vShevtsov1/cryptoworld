package com.harbourtech.cryptoworld.repository;

import com.harbourtech.cryptoworld.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CountryRepository extends JpaRepository<Country, String> {
}
