package com.wesplit.main.services;

import com.wesplit.main.entities.Balance;
import com.wesplit.main.entities.User;
import com.wesplit.main.payloads.BalanceDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface BalanceService {
    void addNewBalance(User user1, User user2);
    Boolean updateExpenseBalance(User user1, User user2, BigDecimal owed);
    Boolean updatePaymentBalance(User user1, User user2, BigDecimal paid);
    BalanceDTO getBalance(User user1, User user2);
    BalanceDTO balanceToBalanceDTO(Balance balance);
}
