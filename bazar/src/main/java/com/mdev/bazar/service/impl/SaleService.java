package com.mdev.bazar.service.impl;

import com.mdev.bazar.dto.request.ProductUpdateRequestDTO;
import com.mdev.bazar.dto.request.SaleCreateRequestDTO;
import com.mdev.bazar.dto.request.SaleItemCreateRequestDTO;
import com.mdev.bazar.dto.response.HighestSaleResponseDTO;
import com.mdev.bazar.dto.response.ProductResponseDTO;
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
        Sale sale = new Sale();
        Sale saved = saleRepo.save(sale);

        saved.setSaleDate(LocalDate.now());

        Double total = 0.0;
        List<SaleItem> saleItems = new ArrayList<>();

        for(SaleItemCreateRequestDTO saleItemDto : dto.saleItems()){
            SaleItem saleItem = new SaleItem();
            saleItem.setSale(saved);

            Product product = productServ.getProduct(saleItemDto.productCode());

            saleItem.setProduct(product);

            saleItem.setQuantity(saleItemDto.quantity());
            saleItem.setUnitPrice(saleItemDto.unitPrice());
            saleItem.setSubtotal(saleItemDto.subtotal());

            total += saleItemDto.subtotal();

            SaleItem created = saleItemServ.createSaleItem(saleItem);

            saleItems.add(created);

            product.setStock(product.getStock() - saleItem.getQuantity());
            ProductUpdateRequestDTO requestDto = new ProductUpdateRequestDTO(null, null, null, product.getStock());
            productServ.updateProduct(product.getProductCode(), requestDto);
        }

        saved.setSaleItems(saleItems);

        saved.setAmount(total);

        Client client = clientServ.getClient(dto.clientId());
        saved.setClient(client);

        Sale updated = saleRepo.save(saved);

        return SaleMapper.toResponseDTO(updated);
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