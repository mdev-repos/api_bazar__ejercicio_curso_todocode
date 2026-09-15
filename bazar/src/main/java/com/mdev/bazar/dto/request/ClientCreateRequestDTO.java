package com.mdev.bazar.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ClientCreateRequestDTO(
        @NotBlank String name,
        @NotBlank String lastName,
        @NotBlank String dni
) {}
