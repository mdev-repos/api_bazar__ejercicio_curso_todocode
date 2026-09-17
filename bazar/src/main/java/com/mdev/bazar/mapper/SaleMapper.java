package com.mdev.bazar.mapper;

import com.mdev.bazar.dto.response.HighestSaleResponseDTO;
import com.mdev.bazar.dto.response.SaleResponseDTO;
import com.mdev.bazar.model.Sale;
import com.mdev.bazar.model.SaleItem;

import java.util.ArrayList;
import java.util.List;

public class SaleMapper {
    public static SaleResponseDTO toResponseDTO(Sale sale){
        return new SaleResponseDTO(
                sale.getSaleId(),
                sale.getSaleDate(),
                sale.getAmount(),
                SaleItemMapper.toResponseDTOList(sale.getSaleItems()),
                sale.getClient().getClientId()
        );
    }

    public static List<SaleResponseDTO> toResponseDTOList(List<Sale> salesList){
        List<SaleResponseDTO> dtoList = new ArrayList<>();
        for(Sale sale : salesList){
            dtoList.add(toResponseDTO(sale));
        }
        return dtoList;
    }

    public static HighestSaleResponseDTO toHighestSaleDTO(Sale sale){
        return new HighestSaleResponseDTO(
            sale.getSaleId(),
            sale.getAmount(),
            sale.getSaleItems().size(),
            sale.getClient().getName(),
            sale.getClient().getLastName()
        );
    }
}