package com.mdev.bazar.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SaleCreateRequestDTO(
        @NotNull(message = "no debe ser nulo") List<SaleItemCreateRequestDTO> saleItems,
        @NotNull(message = "no debe ser nulo") Long clientId
) {}