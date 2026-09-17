package com.mdev.bazar.controller;

import com.mdev.bazar.dto.request.ProductCreateRequestDTO;
import com.mdev.bazar.dto.request.ProductUpdateRequestDTO;
import com.mdev.bazar.dto.response.ProductResponseDTO;
import com.mdev.bazar.service.IProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final IProductService productServ;

    public ProductController(IProductService productService){
        this.productServ = productService;
    }

    @PostMapping("/create")
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductCreateRequestDTO dto){
        ProductResponseDTO created = productServ.createProduct(dto);
        URI location = URI.create("/products" + created.productCode());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id){
        ProductResponseDTO product = productServ.getProductById(id);
        if(product == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(product);
    }

    @GetMapping()
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(){
        List<ProductResponseDTO> productList = productServ.getAllProducts();
        return ResponseEntity.ok(productList);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequestDTO dto){
        ProductResponseDTO updated = productServ.updateProduct(id, dto);
        if(updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        productServ.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/low_stock")
    public ResponseEntity<List<ProductResponseDTO>> getLowStockProducts(){
        List<ProductResponseDTO> lowStockList = productServ.getLowStockProducts();
        return ResponseEntity.ok(lowStockList);
    }
}