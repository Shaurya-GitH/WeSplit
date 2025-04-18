package com.wesplit.main.services;

import com.wesplit.main.entities.Expense;
import com.wesplit.main.entities.ExpenseSplit;
import com.wesplit.main.entities.User;
import com.wesplit.main.payloads.ExpenseSplitDTO;
import com.wesplit.main.repositories.ExpenseSplitRepository;
import com.wesplit.main.utils.RedisUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseSplitServiceImpl implements ExpenseSplitService{
    final private ExpenseSplitRepository expenseSplitRepository;
    final private ModelMapper modelMapper;
    final private RedisUtil redisUtil;
    ExpenseSplitServiceImpl(ExpenseSplitRepository expenseSplitRepository,ModelMapper modelMapper,RedisUtil redisUtil){
        this.expenseSplitRepository=expenseSplitRepository;
        this.modelMapper=modelMapper;
        this.redisUtil=redisUtil;
    }
    @Override
    public ExpenseSplit expenseSplitDTOToExpenseSplit(ExpenseSplitDTO expenseSplitDTO) {
        return modelMapper.map(expenseSplitDTO,ExpenseSplit.class);
    }

    @Override
    public ExpenseSplitDTO expenseSplitToExpenseSplitDTO(ExpenseSplit expenseSplit) {
        return modelMapper.map(expenseSplit,ExpenseSplitDTO.class);
    }
    @Override
    public List<ExpenseSplitDTO> getExpenseSplits(Long expenseId) {
        return expenseSplitRepository.findByExpenseExpenseId(expenseId).stream().map((expenseSplit)->expenseSplitToExpenseSplitDTO(expenseSplit)).toList();
    }

    @Override
    public List<Expense> getUnsettledExpenses(User user1, User user2) {
        return expenseSplitRepository.getExpenses(user1, user2,false);
    }

    @Override
    public List<Expense> getSettledExpenses(User user1, User user2) {
        return expenseSplitRepository.getExpenses(user1,user2,true);
    }


}
