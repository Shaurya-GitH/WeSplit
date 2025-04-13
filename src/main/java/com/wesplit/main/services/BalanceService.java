package com.wesplit.main.services;

import com.wesplit.main.entities.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface BalanceService {
    void addNewBalance(User user1, User user2);
    void updateExpenseBalance(User user1, User user2, BigDecimal owed);
    Boolean updatePaymentBalance(User user1, User user2, BigDecimal paid);
}
