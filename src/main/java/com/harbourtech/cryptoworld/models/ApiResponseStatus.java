package com.harbourtech.cryptoworld.models;

import lombok.Getter;

@Getter
public enum ApiResponseStatus {
    SUCCESS("Success", 200),
    CREATED("Created", 201),
    BAD_REQUEST("Bad Request", 400),
    UNAUTHORIZED("Unauthorized", 401),
    FORBIDDEN("Forbidden", 403),
    NOT_FOUND("Not Found", 404),
    INTERNAL_SERVER_ERROR("Internal Server Error", 500),
    SERVICE_UNAVAILABLE("Service Unavailable", 503);

    private final String description;
    private final int statusCode;

    ApiResponseStatus(String description, int statusCode) {
        this.description = description;
        this.statusCode = statusCode;
    }

}
