package com.mdev.bazar.dto.response;

public record HighestSaleResponseDTO(
   Long saleId,
   Double total,
   Integer productQuantity,
   String clientName,
   String clientLastName
) {}