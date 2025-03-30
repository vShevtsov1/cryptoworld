package com.harbourtech.cryptoworld.controller;

import com.harbourtech.cryptoworld.dto.CompanyCreateDTO;
import com.harbourtech.cryptoworld.entity.Company;
import com.harbourtech.cryptoworld.models.ApiResponseStatus;
import com.harbourtech.cryptoworld.service.CompanyService;
import com.harbourtech.cryptoworld.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;


    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Company>> createCompany(
            @ModelAttribute CompanyCreateDTO company,
            Authentication authentication){

        ApiResponse<Company> response = companyService.createNewCompany(company, authentication.getPrincipal().toString());

        if (!response.getStatus().equals(ApiResponseStatus.SUCCESS)) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<Company>>> getMyCompanies(Authentication authentication){
        ApiResponse<List<Company>> response = companyService.getUserCompanies(authentication.getPrincipal().toString());

        if (!response.getStatus().equals(ApiResponseStatus.SUCCESS)) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<ApiResponse<Company>> getMyCompanies(@PathVariable String companyId, Authentication authentication){
        ApiResponse<Company> response = companyService.getMyCompany(authentication.getPrincipal().toString(),companyId);

        if (!response.getStatus().equals(ApiResponseStatus.SUCCESS)) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

}
