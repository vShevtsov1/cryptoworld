package com.harbourtech.cryptoworld.controller;

import com.harbourtech.cryptoworld.dto.MarketSearchResultDTO;
import com.harbourtech.cryptoworld.dto.MarketSellDTO;
import com.harbourtech.cryptoworld.dto.PixelDTO;
import com.harbourtech.cryptoworld.entity.Market;
import com.harbourtech.cryptoworld.models.ApiResponseStatus;
import com.harbourtech.cryptoworld.service.MarketService;
import com.harbourtech.cryptoworld.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/market")
public class MarketController {

    @Autowired
    private MarketService marketService;


    @PostMapping("/sell")
    public ResponseEntity<ApiResponse<Market>> sellCompany(
            @RequestBody MarketSellDTO sell,
            Authentication authentication
    ) {
        String userId = authentication.getPrincipal().toString();

        try {
            ApiResponse<Market> response = marketService.sellCompany(sell, userId);

            if (!response.getStatus().equals(ApiResponseStatus.SUCCESS)) {
                return ResponseEntity.badRequest().body(response);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ApiResponseStatus.BAD_REQUEST, "Unexpected error occurred: " + e.getMessage()));
        }
    }

    @DeleteMapping("/remove/{companyId}")
    public ResponseEntity<ApiResponse<String>> removeFromMarket(
            @PathVariable String companyId,
            Authentication authentication
    ) {
        String userId = authentication.getPrincipal().toString();

        ApiResponse<String> response = marketService.removeCompanyFromMarket(companyId, userId);

        if (!response.getStatus().equals(ApiResponseStatus.SUCCESS)) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<MarketSearchResultDTO>>> searchMarket(
            @RequestParam(defaultValue = "") String country,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sort
    ) {
        return ResponseEntity.ok(marketService.getMarketsByCountry(country, page, size,sort));
    }

    @GetMapping("/order/{orderId}/pixels")
    public ResponseEntity<ApiResponse<List<PixelDTO>>> getOrderPixels(@PathVariable Long orderId) {
        return ResponseEntity.ok(marketService.getPixelsByOrderId(orderId));
    }




}
