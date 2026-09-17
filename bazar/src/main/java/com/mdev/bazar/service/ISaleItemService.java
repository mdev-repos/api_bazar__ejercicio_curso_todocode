package com.mdev.bazar.service;

import com.mdev.bazar.dto.response.ProductResponseDTO;
import com.mdev.bazar.model.SaleItem;

import java.util.List;

public interface ISaleItemService {
    SaleItem createSaleItem(SaleItem saleItem);
    List<ProductResponseDTO> getSaleProductsBySaleId(Long saleId);
}