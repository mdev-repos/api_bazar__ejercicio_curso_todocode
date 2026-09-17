package com.mdev.bazar.repository;

import com.mdev.bazar.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ISaleRepository extends JpaRepository<Sale, Long> {
    // Get Total Amount by Sale Date
    @Query("SELECT SUM(s.amount) FROM Sale s WHERE s.saleDate = :saleDate")
    Double findSalesAmountByDate(@Param("saleDate") LocalDate saleDate);

    // Get the Highest Amount Sale
    @Query("SELECT s FROM Sale s WHERE s.amount = (SELECT MAX(s.amount) FROM Sale s)")
    Sale findHighestSale();
}