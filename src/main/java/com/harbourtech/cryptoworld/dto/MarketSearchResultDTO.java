package com.harbourtech.cryptoworld.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketSearchResultDTO {
    private Long orderId;
    private UUID companyId;
    private String companyName;
    private String companyDescription;
    private String companyImage;
    private BigDecimal price;
    private List<String> countries;
}
