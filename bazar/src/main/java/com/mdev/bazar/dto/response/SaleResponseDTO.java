package com.mdev.bazar.dto.response;

import java.time.LocalDate;
import java.util.List;

public record SaleResponseDTO(
        Long saleId,
        LocalDate saleDate,
        Double amount,
        List<SaleItemResponseDTO> saleItems,
        Long clientId
) {}
