package com.titta.api.features.sale.service;

import com.titta.api.features.sale.dto.request.SaleRequestDto;
import com.titta.api.features.sale.dto.response.SaleResponseDto;

public interface SaleService {
    SaleResponseDto realizarVenta(SaleRequestDto request);
}