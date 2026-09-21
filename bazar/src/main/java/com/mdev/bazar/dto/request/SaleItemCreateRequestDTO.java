package com.mdev.bazar.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SaleItemCreateRequestDTO(
        @NotNull(message = "no debe ser nulo") Long productCode,
        @Positive(message = "debe ser mayor que 0") Double quantity,
        @Positive(message = "debe ser mayor que 0") Double unitPrice,
        @Positive(message = "debe ser mayor que 0") Double subtotal
) {}