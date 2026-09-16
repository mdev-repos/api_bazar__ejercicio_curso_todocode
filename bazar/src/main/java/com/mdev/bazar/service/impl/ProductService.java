package com.mdev.bazar.service.impl;

import com.mdev.bazar.dto.request.ProductCreateRequestDTO;
import com.mdev.bazar.dto.request.ProductUpdateRequestDTO;
import com.mdev.bazar.dto.response.ProductResponseDTO;
import com.mdev.bazar.mapper.ProductMapper;
import com.mdev.bazar.model.Product;
import com.mdev.bazar.repository.IProductRepository;
import com.mdev.bazar.service.IProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductService {
    private final IProductRepository productRepo;

    public ProductService(IProductRepository IProductRepository){
        this.productRepo = IProductRepository;
    }

    @Override
    public ProductResponseDTO createProduct(ProductCreateRequestDTO dto) {
        Product product = ProductMapper.toEntity(dto);
        Product saved = productRepo.save(product);
        return ProductMapper.toResponseDTO(saved);
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepo.findById(id).orElse(null);
        if(product == null) return null;
        return ProductMapper.toResponseDTO(product);
    }

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        List<Product> productList = productRepo.findAll();
        return ProductMapper.toResponseDTOList(productList);
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, ProductUpdateRequestDTO dto) {
        Product product = productRepo.findById(id).orElse(null);
        if(product == null) return null;
        ProductMapper.applyProductUpdate(product, dto);
        Product updated = productRepo.save(product);
        return ProductMapper.toResponseDTO(updated);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepo.deleteById(id);
    }

    @Override
    public Product getProduct(Long id) {
        return productRepo.findById(id).orElse(null);
    }

    @Override
    public List<ProductResponseDTO> getLowStockProducts() {
        List<Product> lowStockList = productRepo.findLowStockProducts();
        return ProductMapper.toResponseDTOList(lowStockList);
    }
}