package com.wesplit.main.services;

import com.wesplit.main.entities.ExpenseSplit;
import com.wesplit.main.payloads.ExpenseSplitDTO;
import com.wesplit.main.repositories.ExpenseSplitRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ExpenseSplitServiceImpl implements ExpenseSplitService{
    final private ExpenseSplitRepository expenseSplitRepository;
    final private ModelMapper modelMapper;
    ExpenseSplitServiceImpl(ExpenseSplitRepository expenseSplitRepository,ModelMapper modelMapper){
        this.expenseSplitRepository=expenseSplitRepository;
        this.modelMapper=modelMapper;
    }
    @Override
    public ExpenseSplit expenseSplitDTOToExpenseSplit(ExpenseSplitDTO expenseSplitDTO) {
        return modelMapper.map(expenseSplitDTO,ExpenseSplit.class);
    }

    @Override
    public ExpenseSplitDTO expenseSplitToExpenseSplitDTO(ExpenseSplit expenseSplit) {
        return modelMapper.map(expenseSplit,ExpenseSplitDTO.class);
    }


}
