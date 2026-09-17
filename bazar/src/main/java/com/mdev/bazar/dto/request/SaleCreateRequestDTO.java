package com.mdev.bazar.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SaleCreateRequestDTO(
        @NotNull List<SaleItemCreateRequestDTO> saleItems,
        @NotNull Long clientId
) {}