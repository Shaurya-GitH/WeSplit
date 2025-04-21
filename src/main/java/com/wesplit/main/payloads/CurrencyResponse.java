package com.wesplit.main.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyResponse {
    private Data data;
    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Data{
        private String Base;
        private String target;
        private BigDecimal mid;
    }
}
