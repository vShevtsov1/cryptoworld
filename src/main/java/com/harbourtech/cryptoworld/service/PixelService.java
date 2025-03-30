package com.harbourtech.cryptoworld.service;

import com.harbourtech.cryptoworld.dto.BuyPixels;
import com.harbourtech.cryptoworld.dto.PixelsDTO;
import com.harbourtech.cryptoworld.entity.Company;
import com.harbourtech.cryptoworld.entity.Pixel;
import com.harbourtech.cryptoworld.models.ApiResponseStatus;
import com.harbourtech.cryptoworld.repository.CompanyRepository;
import com.harbourtech.cryptoworld.repository.PixelRepository;
import com.harbourtech.cryptoworld.util.ApiResponse;
import com.harbourtech.cryptoworld.util.TransactionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PixelService {

    @Autowired
    private PixelRepository pixelRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private TransactionValidator transactionValidator;

    @Value("${pixel.price}")
    private BigDecimal price;

    @Value("${owner.adress}")
    private String ownerAddress;


    public ApiResponse<List<Pixel>> reservePixels(List<PixelsDTO> pixelsDTOS){
        if (pixelsDTOS == null || pixelsDTOS.isEmpty()) {
            return ApiResponse.error(ApiResponseStatus.BAD_REQUEST,"No pixels provided");
        }
        List<int[]> coordinates = pixelsDTOS.stream()
                .map(dto -> new int[]{Integer.parseInt(dto.getX()), Integer.parseInt(dto.getY())})
                .toList();

        List<Pixel> foundPixels = pixelRepository.findByXInAndYIn(
                coordinates.stream().map(c -> c[0]).distinct().toList(),
                coordinates.stream().map(c -> c[1]).distinct().toList()
        );

        LocalDateTime now = LocalDateTime.now();

        List<Pixel> alreadyReserved = foundPixels.stream()
                .filter(p -> p.getCompany() != null ||
                        (p.getReserveTill() != null && p.getReserveTill().isAfter(now)))
                .toList();

        alreadyReserved.forEach(Pixel::clearRelations);
        if (!alreadyReserved.isEmpty()) {
            return ApiResponse.build(ApiResponseStatus.BAD_REQUEST,"Some pixels are already reserved", alreadyReserved);
        }
        LocalDateTime reserveTill = LocalDateTime.now().plusMinutes(5);
        for (Pixel pixel : foundPixels) {
            pixel.setReserveTill(reserveTill);
        }
        pixelRepository.saveAll(foundPixels);
        foundPixels.forEach(Pixel::clearRelations);
        return ApiResponse.success("Pixels reserved for 5 minutes", foundPixels);
    }

    public ApiResponse<Object> buyPixels(BuyPixels buyPixels){
        List<int[]> coordinates = buyPixels.getPixels().stream()
                .map(dto -> new int[]{Integer.parseInt(dto.getX()), Integer.parseInt(dto.getY())})
                .toList();

        List<Pixel> foundPixels = pixelRepository.findByXInAndYIn(
                coordinates.stream().map(c -> c[0]).distinct().toList(),
                coordinates.stream().map(c -> c[1]).distinct().toList()
        );

        List<Pixel> sold = foundPixels.stream()
                .filter(p -> p.getCompany() != null)
                .toList();

        if(!sold.isEmpty()){
            sold.forEach(Pixel::clearRelations);
            return ApiResponse.build(ApiResponseStatus.BAD_REQUEST,"Some pixels are already sold", sold);
        }

        Company company = companyRepository.findById(UUID.fromString(buyPixels.getCompanyId()))
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        TransactionValidator.TransactionInfo info = transactionValidator.getTransactionInfo(buyPixels.getTxHash());


        BigDecimal expectedTotal = price.multiply(BigDecimal.valueOf(foundPixels.size()));
        BigDecimal actualPaid = info.getAmountEth();

        if(actualPaid.compareTo(expectedTotal) == 0 && info.getTo().equals(ownerAddress)){

            for (Pixel pixel : foundPixels) {
                pixel.setCompany(company);
                pixel.setReserveTill(null);
            }

            pixelRepository.saveAll(foundPixels);

            return ApiResponse.success("Pixels successfully purchased", null);
        }
        else {
            return ApiResponse.error(ApiResponseStatus.BAD_REQUEST,"Incorrect payment amount. Expected: " + expectedTotal + " ETH, but paid: " + actualPaid + " ETH");

        }
    }



}
