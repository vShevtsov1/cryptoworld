package com.harbourtech.cryptoworld.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.harbourtech.cryptoworld.models.ApiResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {


    private ApiResponseStatus status;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;


    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(ApiResponseStatus.SUCCESS, message, data);
    }
    public static <T> ApiResponse<T> build(ApiResponseStatus status,String message, T data) {
        return new ApiResponse<>(status, message, data);
    }

    public static <T> ApiResponse<T> error(ApiResponseStatus status, String message) {
        return new ApiResponse<>(status, message, null);
    }
}
