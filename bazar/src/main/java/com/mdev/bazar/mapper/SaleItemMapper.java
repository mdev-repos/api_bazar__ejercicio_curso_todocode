package com.mdev.bazar.mapper;

import com.mdev.bazar.dto.response.SaleItemResponseDTO;
import com.mdev.bazar.model.SaleItem;

import java.util.ArrayList;
import java.util.List;

public class SaleItemMapper {
    public static SaleItemResponseDTO toResponseDTO(SaleItem saleItem){
        return new SaleItemResponseDTO(
                saleItem.getSaleItemId(),
                saleItem.getSale().getSaleId(),
                saleItem.getProduct().getProductCode(),
                saleItem.getQuantity(),
                saleItem.getUnitPrice(),
                saleItem.getSubtotal()
        );
    }

    public static List<SaleItemResponseDTO> toResponseDTOList(List<SaleItem> saleItems){
        List<SaleItemResponseDTO> saleItemResponseDTOList = new ArrayList<>();
        for(SaleItem item : saleItems){
            saleItemResponseDTOList.add(toResponseDTO(item));
        }
        return saleItemResponseDTOList;
    }
}
