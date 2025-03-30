package com.harbourtech.cryptoworld.service;

import com.harbourtech.cryptoworld.dto.MarketSearchResultDTO;
import com.harbourtech.cryptoworld.dto.MarketSellDTO;
import com.harbourtech.cryptoworld.dto.PixelDTO;
import com.harbourtech.cryptoworld.entity.Company;
import com.harbourtech.cryptoworld.entity.Market;
import com.harbourtech.cryptoworld.entity.Pixel;
import com.harbourtech.cryptoworld.entity.User;
import com.harbourtech.cryptoworld.models.ApiResponseStatus;
import com.harbourtech.cryptoworld.repository.CompanyRepository;
import com.harbourtech.cryptoworld.repository.MarketRepository;
import com.harbourtech.cryptoworld.repository.PixelRepository;
import com.harbourtech.cryptoworld.repository.UserRepository;
import com.harbourtech.cryptoworld.util.ApiResponse;
import com.harbourtech.cryptoworld.util.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MarketService {

    @Autowired
    private MarketRepository marketRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PixelRepository pixelRepository;


    public ApiResponse<Market> sellCompany(MarketSellDTO sell, String identifier) {
        UUID companyId;


        try {
            companyId = UUID.fromString(sell.getCompanyId());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ApiResponse.error(ApiResponseStatus.BAD_REQUEST, "Invalid UUID format");
        }

        Company company = companyRepository.findById(companyId).orElse(null);
        if (company == null) {
            return ApiResponse.error(ApiResponseStatus.BAD_REQUEST, "Company not found");
        }
        Optional<Market> existingMarket = marketRepository.findByCompanyId(companyId);
        if (existingMarket.isPresent()) {
            return ApiResponse.error(ApiResponseStatus.BAD_REQUEST, "This company is already listed on the market");
        }


        User user = userRepository.findByPublicAddress(identifier).orElse(null);
        if (user == null) {
            return ApiResponse.error(ApiResponseStatus.BAD_REQUEST, "User not found");
        }

        if (!company.getUser().getId().equals(user.getId())) {
            return ApiResponse.error(ApiResponseStatus.BAD_REQUEST, "You are not the owner of this company");
        }

        Market market = new Market(company, sell.getSellPrice());
        marketRepository.save(market);

        market.getCompany().setPixels(null);
        market.getCompany().setUser(null);

        return ApiResponse.success("Company successfully listed on the market", market);
    }

    public ApiResponse<String> removeCompanyFromMarket(String companyIdStr, String identifier) {
        UUID companyId;

        try {
            companyId = UUID.fromString(companyIdStr);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(ApiResponseStatus.BAD_REQUEST, "Invalid UUID format");
        }

        Company company = companyRepository.findById(companyId).orElse(null);
        if (company == null) {
            return ApiResponse.error(ApiResponseStatus.BAD_REQUEST, "Company not found");
        }

        if (!company.getUser().getPublicAddress().equals(identifier)) {
            return ApiResponse.error(ApiResponseStatus.BAD_REQUEST, "You are not the owner of this company");
        }

        Optional<Market> market = marketRepository.findByCompanyId(companyId);
        if (market.isEmpty()) {
            return ApiResponse.error(ApiResponseStatus.BAD_REQUEST, "This company is not listed on the market");
        }

        marketRepository.delete(market.get());

        return ApiResponse.success("Company successfully removed from the market", null);
    }

    public ApiResponse<Page<MarketSearchResultDTO>> getMarketsByCountry(String country, int page, int size,String sort) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "published"));

        Page<Market> marketPage = marketRepository.findByCountryNameLike(country, pageable);

        Page<MarketSearchResultDTO> dtoPage = marketPage.map(market -> {
            Company company = market.getCompany();

            List<String> countries = company.getPixels().stream()
                    .map(p -> p.getCountry().getId())
                    .distinct()
                    .toList();

            return new MarketSearchResultDTO(
                    market.getId(),
                    company.getId(),
                    company.getName(),
                    company.getDescription(),
                    company.getImage(),
                    market.getPrice(),
                    countries
            );
        });

        return ApiResponse.success("Market results", dtoPage);
    }

    public ApiResponse<List<PixelDTO>> getPixelsByOrderId(Long orderId) {
        Optional<Market> marketOpt = marketRepository.findById(orderId);

        if (marketOpt.isEmpty()) {
            return ApiResponse.error(ApiResponseStatus.BAD_REQUEST, "Order not found");
        }

        Company company = marketOpt.get().getCompany();
        List<Pixel> pixels = company.getPixels();

        List<PixelDTO> pixelDTOs = pixels.stream()
                .map(p -> new PixelDTO(p.getX(), p.getY(), p.getCountry().getName()))
                .toList();

        return ApiResponse.success("Pixels for order #" + orderId, pixelDTOs);
    }




}
