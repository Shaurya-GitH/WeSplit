package com.wesplit.main.services;

import com.wesplit.main.entities.Expense;
import com.wesplit.main.entities.User;
import com.wesplit.main.payloads.ExpenseDTO;
import com.wesplit.main.payloads.ExpenseResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ExpenseService {
    ExpenseResponseDTO createExpense(ExpenseDTO expenseDTO, String userEmail1, String userEmail2);
    ExpenseResponseDTO expenseToExpenseResponseDTO(Expense expense);
    Expense expenseDTOToExpense(ExpenseDTO expenseDTO);
    List<ExpenseResponseDTO> getUnsettledExpenses(String email1,String email2);
    List<ExpenseResponseDTO> getSettledExpenses(String email1, String email2);
    void settleExpenses(User user1, User user2);
}
