package com.mdev.bazar.service;

import com.mdev.bazar.dto.request.ProductCreateRequestDTO;
import com.mdev.bazar.dto.request.ProductUpdateRequestDTO;
import com.mdev.bazar.dto.response.ProductResponseDTO;
import com.mdev.bazar.model.Product;

import java.util.List;

public interface IProductService {
    ProductResponseDTO createProduct(ProductCreateRequestDTO dto);
    ProductResponseDTO getProductById(Long id);
    List<ProductResponseDTO> getAllProducts();
    ProductResponseDTO updateProduct(Long id, ProductUpdateRequestDTO dto);
    void deleteProduct(Long id);
    Product getProduct(Long id);
    List<ProductResponseDTO> getLowStockProducts();
}