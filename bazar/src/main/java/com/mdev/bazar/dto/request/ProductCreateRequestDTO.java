package com.mdev.bazar.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductCreateRequestDTO(
        @NotBlank String name,
        @NotBlank String brand,
        @PositiveOrZero Double price,
        @PositiveOrZero Double stock
) {
}
