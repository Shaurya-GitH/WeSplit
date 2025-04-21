package com.wesplit.main.services;

import com.wesplit.main.payloads.BalanceDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface CurrencyService {
    BigDecimal updateConversion(BigDecimal money,String from);
    BalanceDTO displayBalance(BalanceDTO balance, String to);
}
