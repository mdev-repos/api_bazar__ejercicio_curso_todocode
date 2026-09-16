package com.mdev.bazar.service.impl;

import com.mdev.bazar.model.SaleItem;
import com.mdev.bazar.repository.ISaleItemRepository;
import com.mdev.bazar.service.ISaleItemService;
import org.springframework.stereotype.Service;

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
}
