package com.wesplit.main.services;

import com.wesplit.main.entities.Balance;
import com.wesplit.main.entities.Expense;
import com.wesplit.main.entities.ExpenseSplit;
import com.wesplit.main.entities.User;
import com.wesplit.main.exceptions.InvalidInputException;
import com.wesplit.main.exceptions.TransactionFailedException;
import com.wesplit.main.payloads.ExpenseResponseDTO;
import com.wesplit.main.payloads.GroupExpenseDTO;
import com.wesplit.main.repositories.BalanceRepository;
import com.wesplit.main.repositories.ExpenseRepository;
import com.wesplit.main.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class GroupExpenseServiceImpl implements GroupExpenseService{
    private final UserService userService;
    private final ExpenseService expenseService;
    private final BalanceRepository balanceRepository;
    private final BalanceService balanceService;
    private final ExpenseRepository expenseRepository;
    private final RedisUtil redisUtil;
    private final RedisTemplate redisTemplate;

    public GroupExpenseServiceImpl(UserService userService, ExpenseService expenseService, BalanceRepository balanceRepository, BalanceService balanceService, ExpenseRepository expenseRepository, RedisUtil redisUtil, RedisTemplate redisTemplate) {
        this.userService = userService;
        this.expenseService = expenseService;
        this.balanceRepository = balanceRepository;
        this.balanceService = balanceService;
        this.expenseRepository = expenseRepository;
        this.redisUtil = redisUtil;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
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
        if(amount.compareTo(totalpaid)!=0){
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
            try{
                BigDecimal netDebt= groupExpenseDTO.getOwe().get(key).subtract(payments.get(key));
                debt.put(user,netDebt);
            }
            catch (NullPointerException e){
                log.error(e.getMessage());
                throw new InvalidInputException("payments and owe","not matching");
            }

        }
        expense.setSplitList(expenseSplitList);
        expense.setCreatedAt(LocalDate.now());
//        expense.setExpenseType(ExpenseType.GROUP);
        HashMap<User,BigDecimal> groupDebtTable= this.createDebtTable(groupExpenseDTO.getGroupId());
        Boolean settled= balanceService.updateGroupExpenseBalance(groupDebtTable,debt,groupExpenseDTO.getCurrency(),groupExpenseDTO.getGroupId());
        if (settled){
            expense.setSettled(Boolean.TRUE);
            this.settleGroupExpenses(groupExpenseDTO.getGroupId());
        }
        try{
            expenseRepository.save(expense);
            //invalidating cache
            redisTemplate.delete(groupExpenseDTO.getGroupId()+"u");
            redisTemplate.delete(groupExpenseDTO.getGroupId()+"s");
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new TransactionFailedException("failed to save group expense");
        }
        return expenseService.expenseToExpenseResponseDTO(expense);
    }

    //helper method
    @Override
    public HashMap<User, BigDecimal> createDebtTable(Long groupId) {
        List<Balance> balances= balanceRepository.findByGroupId(groupId);
        HashMap<User,Integer> indexes= new HashMap<>();
        HashSet<User> users=new HashSet<>();
        for(Balance balance:balances){
            users.add(balance.getUser1());
            users.add(balance.getUser2());
        }
        BigDecimal[][] debtGraph =new BigDecimal[users.size()][users.size()];
        int i=0;
        for(User user:users){
            indexes.put(user,i);
            debtGraph[i][i]=BigDecimal.ZERO;
            i++;
        }
        for(Balance balance:balances){
            debtGraph[indexes.get(balance.getUser1())][indexes.get(balance.getUser2())]=balance.getOneOweTwo();
            debtGraph[indexes.get(balance.getUser2())][indexes.get(balance.getUser1())]=balance.getTwoOweOne();
        }
        HashMap<User,BigDecimal> finalDebt=new HashMap<>();
        for(User user:users){
           Integer index= indexes.get(user);
            BigDecimal net=BigDecimal.ZERO;
           for(int j=0;j< users.size();j++){
               net= net.add(debtGraph[index][j]);
               net=net.subtract(debtGraph[j][index]);
           }
           finalDebt.put(user,net);
        }
        return finalDebt;
    }

    @Override
    public void settleGroupExpenses(Long groupId) {
        List<Expense> expenses= expenseRepository.findByGroupId(groupId);
        expenses.stream().forEach((expense)->expense.setSettled(Boolean.TRUE));
        expenseRepository.saveAll(expenses);
        redisTemplate.delete(groupId+"u");
        redisTemplate.delete(groupId+"s");
    }

    @Override
    public List<ExpenseResponseDTO> getUnsettledExpenses(Long groupId) {
        List<ExpenseResponseDTO> expenseResponseDTOs= redisUtil.getListValue(groupId+"u", ExpenseResponseDTO.class);
        if(expenseResponseDTOs==null){
            List<Expense> expenseList=expenseRepository.findByGroupIdAndSettled(groupId,false);
            expenseResponseDTOs= expenseList.stream().map(expenseService::expenseToExpenseResponseDTO).toList();
            redisUtil.setValue(groupId+"u",expenseResponseDTOs,1000L);
        }
        return expenseResponseDTOs;
    }

    @Override
    public List<ExpenseResponseDTO> getSettledExpenses(Long groupId) {
        List<ExpenseResponseDTO> expenseResponseDTOs= redisUtil.getListValue(groupId+"s", ExpenseResponseDTO.class);
        if(expenseResponseDTOs==null){
            List<Expense> expenseList=expenseRepository.findByGroupIdAndSettled(groupId,true);
            expenseResponseDTOs= expenseList.stream().map(expenseService::expenseToExpenseResponseDTO).toList();
            redisUtil.setValue(groupId+"s",expenseResponseDTOs,1000L);
        }
        return expenseResponseDTOs;
    }

    @Override
    public Expense groupExpenseDTOtoExpense(GroupExpenseDTO groupExpenseDTO) {
        return Expense.builder()
                .description(groupExpenseDTO.getDescription())
                .amount(groupExpenseDTO.getAmount())
                .currency(groupExpenseDTO.getCurrency())
                .settled(Boolean.FALSE)
                .groupId(groupExpenseDTO.getGroupId())
                .build();
    }
}
