package com.mdev.bazar.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ClientCreateRequestDTO(
        @NotBlank(message = "no debe estar vacío") String name,
        @NotBlank(message = "no debe estar vacío") String lastName,
        @NotBlank(message = "no debe estar vacío") String dni
) {}