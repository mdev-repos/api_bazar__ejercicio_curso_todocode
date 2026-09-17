package com.mdev.bazar.service.impl;

import com.mdev.bazar.dto.response.ProductResponseDTO;
import com.mdev.bazar.mapper.ProductMapper;
import com.mdev.bazar.model.SaleItem;
import com.mdev.bazar.repository.ISaleItemRepository;
import com.mdev.bazar.service.ISaleItemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleItemService implements ISaleItemService {
    private final ISaleItemRepository saleItemRepo;

    public SaleItemService(ISaleItemRepository saleItemRepository){
        this.saleItemRepo = saleItemRepository;
    }

    @Override
    public SaleItem createSaleItem(SaleItem saleItem) {
        return saleItemRepo.save(saleItem);
    }

    @Override
    public List<ProductResponseDTO> getSaleProductsBySaleId(Long saleId) {
        return ProductMapper.toResponseDTOList(saleItemRepo.findProductsBySaleId(saleId));
    }
}