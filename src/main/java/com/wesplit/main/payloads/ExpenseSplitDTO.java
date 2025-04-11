package com.wesplit.main.payloads;

import com.wesplit.main.entities.Expense;
import com.wesplit.main.entities.User;

import java.math.BigDecimal;

public class ExpenseSplitDTO {
    private Long expenseSplitId;
    private BigDecimal owed;
    private BigDecimal paid;
    private User user;
    private Expense expense;
}
