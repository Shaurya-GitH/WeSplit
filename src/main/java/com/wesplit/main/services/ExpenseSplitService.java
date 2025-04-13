package com.wesplit.main.services;

import com.wesplit.main.entities.Expense;
import com.wesplit.main.entities.ExpenseSplit;
import com.wesplit.main.entities.User;
import com.wesplit.main.payloads.ExpenseSplitDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ExpenseSplitService {
    ExpenseSplit expenseSplitDTOToExpenseSplit(ExpenseSplitDTO expenseSplitDTO);
    ExpenseSplitDTO expenseSplitToExpenseSplitDTO(ExpenseSplit expenseSplit);
    List<ExpenseSplitDTO> getExpenseSplits(Long expenseId);
    List<Expense> getUnsettledExpenses(User user1,User user2);
    List<Expense> getSettledExpenses(User user1,User user2);
}
