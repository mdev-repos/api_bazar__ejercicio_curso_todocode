package com.mdev.bazar.dto.response;

public record SaleItemResponseDTO(
        Long saleItemId,
        Long saleId,
        Long productId,
        Double quantity,
        Double unitPrice,
        Double subtotal
) {}