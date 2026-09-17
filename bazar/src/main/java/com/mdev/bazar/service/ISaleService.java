package com.mdev.bazar.service;

import com.mdev.bazar.dto.request.SaleCreateRequestDTO;
import com.mdev.bazar.dto.response.HighestSaleResponseDTO;
import com.mdev.bazar.dto.response.ProductResponseDTO;
import com.mdev.bazar.dto.response.SaleResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface ISaleService {
    SaleResponseDTO createSale(SaleCreateRequestDTO dto);
    SaleResponseDTO getSaleById(Long id);
    List<SaleResponseDTO> getAllSales();
    List<ProductResponseDTO> getSaleProductsBySaleId(Long saleId);
    Double getSalesAmountByDate(LocalDate saleDate);
    HighestSaleResponseDTO getHighestSaleData();
}