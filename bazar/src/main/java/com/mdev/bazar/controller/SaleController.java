package com.mdev.bazar.controller;

import com.mdev.bazar.dto.request.SaleCreateRequestDTO;
import com.mdev.bazar.dto.response.SaleResponseDTO;
import com.mdev.bazar.service.ISaleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/sales")
public class SaleController {
    private final ISaleService saleServ;

    public SaleController(ISaleService saleService){
        this.saleServ = saleService;
    }

    @PostMapping("/create")
    public ResponseEntity<SaleResponseDTO> createSale(@Valid @RequestBody SaleCreateRequestDTO dto){
        SaleResponseDTO created = saleServ.createSale(dto);
        URI location = URI.create("/products" + created.saleId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponseDTO> getSaleById(@PathVariable Long id){
        SaleResponseDTO sale = saleServ.getSaleById(id);
        if(sale == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(sale);
    }

    @GetMapping()
    public ResponseEntity<List<SaleResponseDTO>> getAllSales(){
        List<SaleResponseDTO> sales = saleServ.getAllSales();
        return ResponseEntity.ok(sales);
    }
}
