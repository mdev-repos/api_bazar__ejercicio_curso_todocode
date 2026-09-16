package com.mdev.bazar.dto.response;

public record ProductResponseDTO(
        Long productCode,
        String name,
        String brand,
        Double price,
        Double stock
) {
}
