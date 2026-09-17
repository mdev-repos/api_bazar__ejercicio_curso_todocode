package com.mdev.bazar.dto.request;

public record ProductUpdateRequestDTO(
        String name,
        String brand,
        Double price,
        Double stock
) {}