package com.duoc.backendS8.dto;

import java.util.List;

public record LoginResponse(String token, String tokenType, long expiresInSeconds, List<String> roles) {
}
