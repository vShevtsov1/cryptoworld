package com.harbourtech.cryptoworld.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyPixels {

    private List<PixelsDTO> pixels;
    private String companyId;
    private String txHash;
}
