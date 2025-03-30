package com.harbourtech.cryptoworld.repository;

import com.harbourtech.cryptoworld.entity.Market;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MarketRepository extends JpaRepository<Market, Long> {

    Optional<Market> findByCompanyId(UUID companyId);

    @Query(
            value = """
        SELECT DISTINCT m.* FROM market m
        JOIN companies c ON m.company_id = c.id
        JOIN pixels p ON p.company_id = c.id
        JOIN countries ct ON ct.id = p.country_id
        WHERE LOWER(ct.name) LIKE LOWER(CONCAT('%', :country, '%'))
        """,
            countQuery = """
        SELECT COUNT(DISTINCT m.id) FROM market m
        JOIN companies c ON m.company_id = c.id
        JOIN pixels p ON p.company_id = c.id
        JOIN countries ct ON ct.id = p.country_id
        WHERE LOWER(ct.name) LIKE LOWER(CONCAT('%', :country, '%'))
        """,
            nativeQuery = true
    )
    Page<Market> findByCountryNameLike(@Param("country") String country, Pageable pageable);

}
