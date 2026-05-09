package com.duoc.backendS8.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EnviarFacturaRequest(@NotBlank @Email String email) {
}
