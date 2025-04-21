package com.wesplit.main.services;

import com.wesplit.main.entities.Expense;
import com.wesplit.main.entities.ExpenseSplit;
import com.wesplit.main.entities.User;
import com.wesplit.main.exceptions.InvalidInputException;
import com.wesplit.main.exceptions.TooManyRequestsException;
import com.wesplit.main.exceptions.TransactionFailedException;
import com.wesplit.main.payloads.ExpenseDTO;
import com.wesplit.main.payloads.ExpenseResponseDTO;
import com.wesplit.main.payloads.ExpenseType;
import com.wesplit.main.repositories.ExpenseRepository;
import com.wesplit.main.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ExpenseServiceImpl implements ExpenseService{
    final private ExpenseRepository expenseRepository;
    final private ModelMapper modelMapper;
    private final BalanceService balanceService;
    private final UserService userService;
    private final ExpenseSplitService expenseSplitService;
    private final RedisUtil redisUtil;
    private final RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    ExpenseServiceImpl(ExpenseRepository expenseRepository, ModelMapper modelMapper, BalanceService balanceService, UserService userService, ExpenseSplitService expenseSplitService, RedisUtil redisUtil, RedisTemplate<Object, Object> redisTemplate){
        this.expenseRepository=expenseRepository;
        this.modelMapper=modelMapper;
        this.balanceService = balanceService;
        this.userService = userService;
        this.expenseSplitService = expenseSplitService;
        this.redisUtil=redisUtil;
        this.redisTemplate = redisTemplate;
    }
    @Transactional
    @Override
    public ExpenseResponseDTO createExpense(ExpenseDTO expenseDTO, String userEmail1, String userEmail2) {
        //check if user2 is user1
        if(userEmail1.equals(userEmail2)){
            throw new InvalidInputException("user 2 as","yourself");
        }

        //Api throttling
        String value=(String) redisTemplate.opsForValue().get(userEmail1+"_createExpense");
        if(value!=null){
            Integer integer=Integer.valueOf(value);
            if(integer>9){
                throw new TooManyRequestsException(10,"createExpense","60 seconds");
            }
            else{
                redisTemplate.opsForValue().increment(userEmail1+"_createExpense");
            }
        }
        else{
            redisTemplate.opsForValue().increment(userEmail1+"_createExpense");
            redisTemplate.expire(userEmail1+"_createExpense",60L, TimeUnit.SECONDS);
        }

        //method logic
        User user1= userService.getUser(userEmail1);
        User user2=userService.getUser(userEmail2);
        Expense expense=this.expenseDTOToExpense(expenseDTO);
        ExpenseType expenseType= expense.getExpenseType();
        ExpenseSplit expenseSplit1;
        ExpenseSplit expenseSplit2;
        Boolean settled;
        BigDecimal amount=expense.getAmount();
        if(amount.equals(BigDecimal.ZERO))throw new InvalidInputException("amount","less than 0");
        BigDecimal half=amount.divide(BigDecimal.valueOf(2),2,RoundingMode.HALF_UP);
        //dividing the logic based on expense types
        if(expenseType.equals(ExpenseType.SPLIT_EQUALLY_PAIDBY_USER1)){
            expenseSplit1=ExpenseSplit.builder()
                    .expense(expense)
                    .owed(BigDecimal.ZERO)
                    .paid(amount)
                    .user(user1)
                    .build();
            expenseSplit2=ExpenseSplit.builder()
                    .expense(expense)
                    .owed(half)
                    .paid(BigDecimal.ZERO)
                    .user(user2)
                    .build();
            //updating the balance
            settled= balanceService.updateExpenseBalance(user1,user2,half.negate());

        }
        else if(expenseType.equals(ExpenseType.SPLIT_EQUALLY_PAIDBY_USER2)){
            expenseSplit1=ExpenseSplit.builder()
                    .expense(expense)
                    .owed(half)
                    .paid(BigDecimal.ZERO)
                    .user(user1)
                    .build();

            expenseSplit2=ExpenseSplit.builder()
                    .expense(expense)
                    .owed(BigDecimal.ZERO)
                    .paid(amount)
                    .user(user2)
                    .build();
            //updating the balance
            settled= balanceService.updateExpenseBalance(user1,user2,half);

        }
        else if(expenseType.equals(ExpenseType.OWED_USER1)){

            expenseSplit1=ExpenseSplit.builder()
                    .expense(expense)
                    .owed(BigDecimal.ZERO)
                    .paid(amount)
                    .user(user1)
                    .build();

            expenseSplit2=ExpenseSplit.builder()
                    .expense(expense)
                    .owed(amount)
                    .paid(BigDecimal.ZERO)
                    .user(user2)
                    .build();
            //updating the balance
             settled= balanceService.updateExpenseBalance(user1,user2,amount.negate());

        }
        else if (expenseType.equals(ExpenseType.OWED_USER2)) {

            expenseSplit1=ExpenseSplit.builder()
                    .expense(expense)
                    .owed(amount)
                    .paid(BigDecimal.ZERO)
                    .user(user1)
                    .build();

            expenseSplit2=ExpenseSplit.builder()
                    .expense(expense)
                    .owed(BigDecimal.ZERO)
                    .paid(amount)
                    .user(user2)
                    .build();
            //updating the balance
            settled= balanceService.updateExpenseBalance(user1,user2,amount);

        }
        else if(expense.getExpenseType().equals(ExpenseType.CUSTOM)){
            BigDecimal user1paid=expenseDTO.getUser1paid();
            BigDecimal user2paid=expenseDTO.getUser2paid();
            BigDecimal user1owe=expenseDTO.getUser1owe();
            BigDecimal user2owe=expenseDTO.getUser2owe();
            //checking if payment and owe sums match the total amount
            if(!user1paid.add(user2paid).equals(amount))throw new InvalidInputException("payment sum","mismatch");
            if(!user1owe.add(user2owe).equals(amount))throw new InvalidInputException("owe sum","mismatch");
            expenseSplit1=ExpenseSplit.builder()
                         .expense(expense)
                         .user(user1)
                         .owed(user1owe)
                         .paid(user1paid)
                         .build();
            expenseSplit2=ExpenseSplit.builder()
                         .expense(expense)
                         .user(user2)
                         .owed(user2owe)
                         .paid(user2paid)
                         .build();
            //updating the balance
            BigDecimal owed= user1owe.subtract(user1paid);
            settled= balanceService.updateExpenseBalance(user1,user2,owed);
        }
        else{
            throw new InvalidInputException("ExpenseType",expense.getExpenseType()+"");
        }
        //settling the expenses
        if(settled==null || settled){
            this.settleExpenses(user1,user2);
        }
        //adding the expense splits to expense object
        List<ExpenseSplit> newList=new ArrayList<>();
        newList.add(expenseSplit1);
        newList.add(expenseSplit2);
        expense.setSplitList(newList);
        if(settled!=null && settled){
            expense.setSettled(settled);
        }
        else {
            expense.setSettled(false);
        }
        try{
            expenseRepository.save(expense);
            return this.expenseToExpenseResponseDTO(expense);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new TransactionFailedException("Failed to add expense");
        }
    }

    @Override
    public ExpenseResponseDTO expenseToExpenseResponseDTO(Expense expense) {
        return modelMapper.map(expense, ExpenseResponseDTO.class);
    }

    @Override
    public Expense expenseDTOToExpense(ExpenseDTO expenseDTO) {
        return modelMapper.map(expenseDTO,Expense.class);
    }

    @Override
    public List<ExpenseResponseDTO> getUnsettledExpenses(String email1, String email2) {
        List<ExpenseResponseDTO> cache= redisUtil.getListValue(email1+"_"+email2+"_u_expenses", ExpenseResponseDTO.class);
        if(cache!=null) return cache;
        else{
            User user1= userService.getUser(email1);
            User user2= userService.getUser(email2);
            List<Expense> list= expenseSplitService.getUnsettledExpenses(user1,user2);
            List<ExpenseResponseDTO> returning= list.stream().map((this::expenseToExpenseResponseDTO)).toList();
            redisUtil.setValue(email1+"_"+email2+"_u_expenses",returning,300L);
            return returning;
        }
    }

    @Override
    public List<ExpenseResponseDTO> getSettledExpenses(String email1, String email2) {
        List<ExpenseResponseDTO> cache= redisUtil.getListValue(email1+"_"+email2+"_s_expenses", ExpenseResponseDTO.class);
        if(cache!=null){
            return cache;
        }
        else {
            User user1= userService.getUser(email1);
            User user2= userService.getUser(email2);
            List<Expense> list = expenseSplitService.getSettledExpenses(user1, user2);
            List<ExpenseResponseDTO> returning= list.stream().map((this::expenseToExpenseResponseDTO)).toList();
            redisUtil.setValue(email1 + "_" + email2 + "_s_expenses", returning,300L);
            return returning;
        }
    }

    @Transactional
    @Override
    public void settleExpenses(User user1, User user2) {
        try{
            List<Expense> list= expenseSplitService.getUnsettledExpenses(user1,user2);
            list.stream().forEach((expense)-> {
                expense.setSettled(true);
                expenseRepository.save(expense);
            });
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new TransactionFailedException("failed to settle expenses");
        }
    }
}
