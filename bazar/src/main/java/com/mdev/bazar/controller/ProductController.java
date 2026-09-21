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
        return ResponseEntity.ok(productServ.getProductById(id));
    }

    @GetMapping()
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(){
        return ResponseEntity.ok(productServ.getAllProducts());
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequestDTO dto){
        return ResponseEntity.ok(productServ.updateProduct(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        productServ.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/low_stock")
    public ResponseEntity<List<ProductResponseDTO>> getLowStockProducts(){
        return ResponseEntity.ok(productServ.getLowStockProducts());
    }
}