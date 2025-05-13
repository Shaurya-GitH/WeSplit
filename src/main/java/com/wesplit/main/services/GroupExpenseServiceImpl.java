package com.wesplit.main.services;

import com.wesplit.main.entities.Expense;
import com.wesplit.main.entities.ExpenseSplit;
import com.wesplit.main.entities.User;
import com.wesplit.main.exceptions.InvalidInputException;
import com.wesplit.main.payloads.ExpenseResponseDTO;
import com.wesplit.main.payloads.ExpenseType;
import com.wesplit.main.payloads.GroupExpenseDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Service
public class GroupExpenseServiceImpl implements GroupExpenseService{
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final ExpenseService expenseService;

    public GroupExpenseServiceImpl(UserService userService, ModelMapper modelMapper, ExpenseService expenseService) {
        this.userService = userService;
        this.modelMapper=modelMapper;
        this.expenseService = expenseService;
    }

    @Override
    public ExpenseResponseDTO createGroupExpense(GroupExpenseDTO groupExpenseDTO) {
        //checking if payments are equal to amount
        BigDecimal amount= groupExpenseDTO.getAmount();
        HashMap<String,BigDecimal> payments=groupExpenseDTO.getPayments();
        Set<String> keyset= payments.keySet();
        BigDecimal totalpaid=BigDecimal.ZERO;
        for(String key:keyset){
          totalpaid= totalpaid.add(payments.get(key));
        }
        if(!amount.equals(totalpaid)){
            throw new InvalidInputException("payments","not equal to Amount");
        }
        //creating expense splits
        HashMap<User,BigDecimal> debt=new HashMap<>();
        List<ExpenseSplit> expenseSplitList= new ArrayList<>();
        Expense expense=this.groupExpenseDTOtoExpense(groupExpenseDTO);
        for(String key:keyset){
            User user=userService.getUser(key);
            ExpenseSplit expenseSplit= ExpenseSplit.builder()
                    .owed(groupExpenseDTO.getOwe().get(key))
                    .paid(payments.get(key))
                    .user(user)
                    .expense(expense)
                    .build();
            expenseSplitList.add(expenseSplit);
            //making net debt hashmap
            BigDecimal netDebt= groupExpenseDTO.getOwe().get(key).subtract(payments.get(key));
            debt.put(user,netDebt);
        }
        expense.setSplitList(expenseSplitList);
        expense.setCreatedAt(LocalDate.now());
        expense.setExpenseType(ExpenseType.GROUP);
        HashMap<User,BigDecimal> finalDebtTable= this.createDebtTable(keyset,debt);
        return expenseService.expenseToExpenseResponseDTO(expense);
    }

    @Override
    public HashMap<User, BigDecimal> createDebtTable(Set<String> keyset, HashMap<User, BigDecimal> debt) {

        return null;
    }

    @Override
    public Expense groupExpenseDTOtoExpense(GroupExpenseDTO groupExpenseDTO) {
        return modelMapper.map(groupExpenseDTO,Expense.class);
    }
}
