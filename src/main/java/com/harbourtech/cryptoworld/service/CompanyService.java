package com.harbourtech.cryptoworld.service;

import com.harbourtech.cryptoworld.dto.CompanyCreateDTO;
import com.harbourtech.cryptoworld.entity.Company;
import com.harbourtech.cryptoworld.entity.User;
import com.harbourtech.cryptoworld.models.ApiResponseStatus;
import com.harbourtech.cryptoworld.repository.CompanyRepository;
import com.harbourtech.cryptoworld.repository.UserRepository;
import com.harbourtech.cryptoworld.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DigitalOceanS3Uploader digitalOceanS3Uploader;

    @Autowired
    private UserRepository userRepository;

    public ApiResponse<Company> createNewCompany(CompanyCreateDTO companyDTO, String identifier) {
        try {
            if (companyDTO == null || identifier == null || identifier.isBlank()) {
                return ApiResponse.error(ApiResponseStatus.BAD_REQUEST, "Invalid input data");
            }
            Optional<User> optionalUser = userRepository.findByPublicAddress(identifier);
            if (optionalUser.isEmpty()) {
                return ApiResponse.error(ApiResponseStatus.BAD_REQUEST, "User not found with the given identifier");
            }
            User user = optionalUser.get();

            String imagePath;
            try {
                imagePath = digitalOceanS3Uploader.uploadFile(companyDTO.getImage());
            } catch (IOException e) {
                return ApiResponse.error(ApiResponseStatus.BAD_REQUEST, "Failed to upload image: " + e.getMessage());
            }

            Company company = new Company(
                    companyDTO.getName(),
                    companyDTO.getDescription(),
                    companyDTO.getLink(),
                    imagePath,
                    user
            );

            Company savedCompany = companyRepository.save(company);
            savedCompany.clearRelations();
            return ApiResponse.success("Company created", savedCompany);
        } catch (Exception e) {
            return ApiResponse.error(ApiResponseStatus.BAD_REQUEST, "Unexpected error occurred: " + e.getMessage());
        }
    }

    public ApiResponse<List<Company>> getUserCompanies(String identifier) {
        Optional<User> optionalUser = userRepository.findByPublicAddress(identifier);
        if (optionalUser.isEmpty()) {
            return ApiResponse.error(ApiResponseStatus.BAD_REQUEST, "User not found with the given identifier");
        }
        User user = optionalUser.get();
        List<Company> companies = companyRepository.getAllByUser(user);
        companies.forEach(Company::clearRelations);
        return ApiResponse.success("User companies fetched successfully", companies);
    }

    public ApiResponse<Company> getMyCompany(String identifier,String companyId) {
        Optional<User> optionalUser = userRepository.findByPublicAddress(identifier);
        if (optionalUser.isEmpty()) {
            return ApiResponse.error(ApiResponseStatus.NOT_FOUND, "User not found with the given identifier");
        }
        User user = optionalUser.get();
        Company company = companyRepository.getCompanyById(UUID.fromString(companyId));

        if(!company.getUser().equals(user)) {
            return ApiResponse.error(ApiResponseStatus.BAD_REQUEST, "User does not belong to this company");
        }
        company.setUser(null);

        return ApiResponse.success("User company fetched successfully", company);
    }

}
