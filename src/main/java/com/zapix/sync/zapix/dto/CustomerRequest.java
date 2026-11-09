package com.zapix.sync.zapix.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
        @NotBlank(message = "name is required")
        @Size(max = 150, message = "name must have at most 150 characters")
        String name,

        @Pattern(regexp = "\\d{11}|\\d{14}", message = "document must have 11 (CPF) or 14 (CNPJ) digits")
        String document,

        @Email(message = "email is invalid")
        @Size(max = 150, message = "email must have at most 150 characters")
        String email,

        @NotBlank(message = "phone is required")
        @Pattern(regexp = "\\d{10,15}", message = "phone must have 10 to 15 digits, including the country code")
        String phone,

        boolean whatsappOptIn
) {
}
