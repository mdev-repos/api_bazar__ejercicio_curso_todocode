package com.mdev.bazar.dto.response;

public record ClientResponseDTO(
        Long clientId,
        String name,
        String lastName,
        String dni
) {}
