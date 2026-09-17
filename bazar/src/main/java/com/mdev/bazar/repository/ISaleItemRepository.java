package com.mdev.bazar.repository;

import com.mdev.bazar.model.Product;
import com.mdev.bazar.model.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISaleItemRepository extends JpaRepository<SaleItem, Long> {
    // Product list of specific Sale
    @Query("SELECT si.product FROM SaleItem si WHERE si.sale.saleId = :saleId")
    List<Product> findProductsBySaleId(@Param("saleId") Long saleId);
}