package com.wesplit.main.services;

import com.wesplit.main.entities.Expense;
import com.wesplit.main.entities.User;
import com.wesplit.main.payloads.ExpenseResponseDTO;
import com.wesplit.main.payloads.GroupExpenseDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Set;

@Service
public interface GroupExpenseService {
    ExpenseResponseDTO createGroupExpense(GroupExpenseDTO groupExpenseDTO);
    Expense groupExpenseDTOtoExpense(GroupExpenseDTO groupExpenseDTO);
    HashMap<User, BigDecimal> createDebtTable(Long groupId);
}
