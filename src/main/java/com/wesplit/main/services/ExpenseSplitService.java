package com.wesplit.main.services;

import com.wesplit.main.entities.ExpenseSplit;
import com.wesplit.main.payloads.ExpenseSplitDTO;
import org.springframework.stereotype.Service;

@Service
public interface ExpenseSplitService {
    ExpenseSplit expenseSplitDTOToExpenseSplit(ExpenseSplitDTO expenseSplitDTO);
    ExpenseSplitDTO expenseSplitToExpenseSplitDTO(ExpenseSplit expenseSplit);
}
