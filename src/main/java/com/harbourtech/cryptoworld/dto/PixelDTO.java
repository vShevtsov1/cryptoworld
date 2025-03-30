package com.harbourtech.cryptoworld.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PixelDTO {
    private int x;
    private int y;
    private String countryName;
}
