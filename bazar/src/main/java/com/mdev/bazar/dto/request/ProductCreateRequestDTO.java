package com.mdev.bazar.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductCreateRequestDTO(
        @NotBlank(message = "no debe estar vacío") String name,
        @NotBlank(message = "no debe estar vacío") String brand,
        @PositiveOrZero(message = "debe ser mayor que o igual a 0") Double price,
        @PositiveOrZero(message = "debe ser mayor que o igual a 0") Double stock
) {}