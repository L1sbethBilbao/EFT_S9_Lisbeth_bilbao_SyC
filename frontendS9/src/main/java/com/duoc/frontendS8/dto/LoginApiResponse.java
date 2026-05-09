package com.duoc.frontendS8.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LoginApiResponse(String token, String tokenType, long expiresInSeconds, List<String> roles) {
}
