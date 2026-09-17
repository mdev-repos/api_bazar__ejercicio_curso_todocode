package com.mdev.bazar.dto.request;


public record ClientUpdateRequestDTO (
        String name,
        String lastName,
        String dni
) {}