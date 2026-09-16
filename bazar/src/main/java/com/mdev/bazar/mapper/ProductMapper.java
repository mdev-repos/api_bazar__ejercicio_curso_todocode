package com.mdev.bazar.mapper;

import com.mdev.bazar.dto.request.ProductCreateRequestDTO;
import com.mdev.bazar.dto.request.ProductUpdateRequestDTO;
import com.mdev.bazar.dto.response.ProductResponseDTO;
import com.mdev.bazar.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductMapper {
    public static Product toEntity(ProductCreateRequestDTO dto){
        Product product = new Product();
        product.setName(dto.name());
        product.setBrand(dto.brand());
        product.setPrice(dto.price());
        product.setStock(dto.stock());

        return product;
    }

    public static ProductResponseDTO toResponseDTO(Product product){
        return new ProductResponseDTO(
                product.getProductCode(),
                product.getName(),
                product.getBrand(),
                product.getPrice(),
                product.getStock()
        );
    }

    public static List<ProductResponseDTO> toResponseDTOList(List<Product> productList){
        List<ProductResponseDTO> dtoList = new ArrayList<>();

        for(Product product : productList){
            dtoList.add(toResponseDTO(product));
        }

        return dtoList;
    }

    public static void applyProductUpdate(Product product, ProductUpdateRequestDTO dto){
        if(dto.name() != null) product.setName(dto.name());
        if(dto.brand() != null) product.setBrand(dto.brand());
        if(dto.price() != null) product.setPrice(dto.price());
        if (dto.stock() != null) product.setStock(dto.stock());
    }
}
