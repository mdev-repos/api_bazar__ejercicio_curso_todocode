package com.mdev.bazar.service.impl;

import com.mdev.bazar.dto.request.SaleCreateRequestDTO;
import com.mdev.bazar.dto.request.SaleItemCreateRequestDTO;
import com.mdev.bazar.dto.response.HighestSaleResponseDTO;
import com.mdev.bazar.dto.response.ProductResponseDTO;
import com.mdev.bazar.dto.response.SaleResponseDTO;
import com.mdev.bazar.exception.InsufficientStockException;
import com.mdev.bazar.exception.ResourceNotFoundException;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Transactional
    @Override
    public SaleResponseDTO createSale(SaleCreateRequestDTO dto) {
        Client client = clientServ.getClient(dto.clientId());

        Map<Long, Product> productsByCode = new HashMap<>();
        for (SaleItemCreateRequestDTO saleItemDto : dto.saleItems()) {
            Product product = productServ.getProduct(saleItemDto.productCode());
            if (product.getStock() < saleItemDto.quantity())
                throw new InsufficientStockException("No hay suficiente Stock del producto con el codigo " + product.getProductCode() + " (" + product.getName() + ") para la venta solicitada.");
            productsByCode.put(saleItemDto.productCode(), product);
        }

        Sale sale = new Sale();
        sale.setSaleDate(LocalDate.now());
        sale.setClient(client);

        List<SaleItem> saleItems = new ArrayList<>();
        Double total = 0.0;

        for (SaleItemCreateRequestDTO saleItemDto : dto.saleItems()) {
            Product product = productsByCode.get(saleItemDto.productCode());

            SaleItem saleItem = new SaleItem();
            saleItem.setSale(sale);
            saleItem.setProduct(product);
            saleItem.setQuantity(saleItemDto.quantity());
            saleItem.setUnitPrice(saleItemDto.unitPrice());
            saleItem.setSubtotal(saleItemDto.subtotal());
            saleItems.add(saleItem);

            total += saleItemDto.subtotal();

            product.setStock(product.getStock() - saleItem.getQuantity());
        }

        sale.setSaleItems(saleItems);
        sale.setAmount(total);

        Sale saved = saleRepo.save(sale);

        return SaleMapper.toResponseDTO(saved);
    }

    @Override
    public SaleResponseDTO getSaleById(Long id) {
        Sale sale = saleRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Venta no encontrada con el ID: " + id));
        return SaleMapper.toResponseDTO(sale);
    }

    @Override
    public List<SaleResponseDTO> getAllSales() {
        return SaleMapper.toResponseDTOList(saleRepo.findAll());
    }

    @Override
    public List<ProductResponseDTO> getSaleProductsBySaleId(Long saleId) {
        return saleItemServ.getSaleProductsBySaleId(saleId);
    }

    @Override
    public Double getSalesAmountByDate(LocalDate saleDate) {
        return saleRepo.findSalesAmountByDate(saleDate);
    }

    @Override
    public HighestSaleResponseDTO getHighestSaleData() {
        return SaleMapper.toHighestSaleDTO(saleRepo.findHighestSale());
    }
}