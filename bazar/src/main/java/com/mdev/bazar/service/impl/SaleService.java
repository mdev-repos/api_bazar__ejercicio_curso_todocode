package com.mdev.bazar.service.impl;

import com.mdev.bazar.dto.request.ProductUpdateRequestDTO;
import com.mdev.bazar.dto.request.SaleCreateRequestDTO;
import com.mdev.bazar.dto.request.SaleItemCreateRequestDTO;
import com.mdev.bazar.dto.response.SaleResponseDTO;
import com.mdev.bazar.mapper.SaleMapper;
import com.mdev.bazar.model.Client;
import com.mdev.bazar.model.Product;
import com.mdev.bazar.model.Sale;
import com.mdev.bazar.model.SaleItem;
import com.mdev.bazar.repository.ISaleRepository;
import com.mdev.bazar.service.IClientService;
import com.mdev.bazar.service.IProductService;
import com.mdev.bazar.service.ISaleItemService;
import com.mdev.bazar.service.ISaleService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SaleService implements ISaleService {
    private final ISaleRepository saleRepo;
    private final ISaleItemService saleItemServ;
    private final IProductService productServ;
    private final IClientService clientServ;

    public SaleService(ISaleRepository saleRepository,
                       ISaleItemService saleItemService,
                       IProductService productService,
                       IClientService clientService){
        this.saleRepo = saleRepository;
        this.saleItemServ = saleItemService;
        this.productServ = productService;
        this.clientServ = clientService;
    }

    @Override
    public SaleResponseDTO createSale(SaleCreateRequestDTO dto) {
        // Sale instance
        Sale sale = new Sale();

        // Sale Date
        sale.setSaleDate(LocalDate.now());

        // Sale Amount
        Double total = 0.0;

        // Sale Items Instance
        List<SaleItem> saleItems = new ArrayList<>();

        // dtoSaleItems --> saleItems
        for(SaleItemCreateRequestDTO saleItemDto : dto.saleItems()){
            SaleItem saleItem = new SaleItem();
            saleItem.setSale(sale);

            // Get Product from DDBB
            Product product = productServ.getProduct(saleItemDto.productCode());

            // Incorporate to the Sale Item
            saleItem.setProduct(product);

            // Set the rest of the values
            saleItem.setQuantity(saleItemDto.quantity());
            saleItem.setUnitPrice(saleItemDto.unitPrice());
            saleItem.setSubtotal(saleItemDto.subtotal());

            // ADD subtotal to the Ammount
            total += saleItemDto.subtotal();

            // Created the Sale Item into the DDBB
            SaleItem created = saleItemServ.createSaleItem(saleItem);

            // Add the created Sale Item to the SaleItems instance
            saleItems.add(created);

            // Product Stock
            product.setStock(product.getStock() - saleItem.getQuantity());
            ProductUpdateRequestDTO requestDto = new ProductUpdateRequestDTO(null, null, null, product.getStock());
            productServ.updateProduct(product.getProductCode(), requestDto);
        }

        // Set the SaleItems list to the Sale
        sale.setSaleItems(saleItems);

        // Set Sale Amount
        sale.setAmount(total);

        // Get Client from the DDBB and set it to the Sale
        Client client = clientServ.getClient(dto.clientId());
        sale.setClient(client);

        Sale saved = saleRepo.save(sale);

        return SaleMapper.toResponseDTO(saved);
    }

    @Override
    public SaleResponseDTO getSaleById(Long id) {
        Sale sale = saleRepo.findById(id).orElse(null);
        if(sale == null) return null;
        return SaleMapper.toResponseDTO(sale);
    }

    @Override
    public List<SaleResponseDTO> getAllSales() {
        List<Sale> sales = saleRepo.findAll();
        return SaleMapper.toResponseDTOList(sales);
    }
}