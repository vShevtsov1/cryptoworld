package com.harbourtech.cryptoworld.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketSellDTO {

    private String companyId;
    private BigDecimal sellPrice;
}
