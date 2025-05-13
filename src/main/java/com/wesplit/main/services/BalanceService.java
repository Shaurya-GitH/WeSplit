package com.wesplit.main.services;

import com.wesplit.main.entities.Balance;
import com.wesplit.main.entities.User;
import com.wesplit.main.payloads.BalanceDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface BalanceService {
    void addNewBalance(User user1, User user2,Long groupId);
    Boolean updateExpenseBalance(User user1, User user2, BigDecimal intialOwed,String currency);
    Boolean updatePaymentBalance(User user1, User user2, BigDecimal initialPaid,String currency);
    BalanceDTO getBalance(User user1, User user2,String currency);
    BalanceDTO balanceToBalanceDTO(Balance balance);
}
