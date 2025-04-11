package com.wesplit.main.services;

import com.wesplit.main.entities.Expense;
import com.wesplit.main.payloads.ExpenseDTO;
import com.wesplit.main.payloads.ExpenseResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface ExpenseService {
    ExpenseResponseDTO createExpense(ExpenseDTO expenseDTO, String userEmail1, String userEmail2);
    ExpenseResponseDTO expenseToExpenseResponseDTO(Expense expense);
    Expense expenseDTOToExpense(ExpenseDTO expenseDTO);
}
