package com.harbourtech.cryptoworld.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyCreateDTO {

    private UUID id;

    private String name;

    private String description;
    private URL link;
    private MultipartFile image;
}
