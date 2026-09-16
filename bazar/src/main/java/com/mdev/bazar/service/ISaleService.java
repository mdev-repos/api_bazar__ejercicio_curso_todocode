package com.mdev.bazar.service;

import com.mdev.bazar.dto.request.SaleCreateRequestDTO;
import com.mdev.bazar.dto.response.SaleResponseDTO;

import java.util.List;

public interface ISaleService {
    SaleResponseDTO createSale(SaleCreateRequestDTO dto);
    SaleResponseDTO getSaleById(Long id);
    List<SaleResponseDTO> getAllSales();
}