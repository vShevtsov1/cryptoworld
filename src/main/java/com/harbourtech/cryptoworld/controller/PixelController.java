package com.harbourtech.cryptoworld.controller;

import com.harbourtech.cryptoworld.dto.BuyPixels;
import com.harbourtech.cryptoworld.dto.PixelsDTO;
import com.harbourtech.cryptoworld.entity.Pixel;
import com.harbourtech.cryptoworld.models.ApiResponseStatus;
import com.harbourtech.cryptoworld.repository.PixelRepository;
import com.harbourtech.cryptoworld.service.PixelService;
import com.harbourtech.cryptoworld.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pixel")
public class PixelController {

    @Autowired
    private PixelService pixelService;


    @PostMapping("/reserve")
    public ResponseEntity<ApiResponse<List<Pixel>>> reservePixels(@RequestBody List<PixelsDTO> pixels) {
        ApiResponse<List<Pixel>> response = pixelService.reservePixels(pixels);

        if (!response.getStatus().equals(ApiResponseStatus.SUCCESS)) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/buy")
    public ResponseEntity<ApiResponse<Object>> buyPixels(@RequestBody BuyPixels request) {
        try {
            ApiResponse<Object> response = pixelService.buyPixels(request);

            if (!response.getStatus().equals(ApiResponseStatus.SUCCESS)) {
                return ResponseEntity.badRequest().body(response);
            }

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ApiResponseStatus.BAD_REQUEST,e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ApiResponseStatus.BAD_REQUEST,"Unexpected error: " + e.getMessage()));
        }
    }
}
