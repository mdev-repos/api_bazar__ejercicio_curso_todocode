package com.mdev.bazar.repository;

import com.mdev.bazar.model.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISaleItemRepository extends JpaRepository<SaleItem, Long> {
}
