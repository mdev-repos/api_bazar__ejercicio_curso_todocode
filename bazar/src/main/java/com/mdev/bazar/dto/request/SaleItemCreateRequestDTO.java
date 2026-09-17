package com.mdev.bazar.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SaleItemCreateRequestDTO(
        @NotNull Long productCode,
        @Positive Double quantity,
        @Positive Double unitPrice,
        @Positive Double subtotal
) {}