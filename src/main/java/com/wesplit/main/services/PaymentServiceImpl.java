package com.wesplit.main.services;

import com.wesplit.main.entities.Payment;
import com.wesplit.main.entities.User;
import com.wesplit.main.exceptions.InvalidInputException;
import com.wesplit.main.exceptions.TooManyRequestsException;
import com.wesplit.main.exceptions.TransactionFailedException;
import com.wesplit.main.payloads.PaymentDTO;
import com.wesplit.main.payloads.PaymentResponseDTO;
import com.wesplit.main.repositories.PaymentRepository;
import com.wesplit.main.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService{
    private final BalanceService balanceService;
    private final ExpenseService expenseService;
    private final RedisTemplate<Object, Object> redisTemplate;
    PaymentRepository paymentRepository;
    UserService userService;
    ModelMapper modelMapper;
    PaymentServiceImpl(PaymentRepository paymentRepository, UserService userService, ModelMapper modelMapper, BalanceService balanceService, ExpenseService expenseService, RedisTemplate<Object, Object> redisTemplate){
        this.paymentRepository=paymentRepository;
        this.userService=userService;
        this.modelMapper=modelMapper;
        this.balanceService = balanceService;
        this.expenseService = expenseService;
        this.redisTemplate = redisTemplate;
    }
    @Transactional
    @Override
    public void createPayment(PaymentDTO paymentDTO, String email1, String email2) {
        //check if user2 is user1
        if(email1.equals(email2)){
            throw new InvalidInputException("user 2 as","yourself");
        }

        //Api throttling
        String value=(String) redisTemplate.opsForValue().get(email1+"_createPayment");
        if(value!=null){
            Integer integer=Integer.valueOf(value);
            if(integer>9){
                throw new TooManyRequestsException(10,"createPayment","60 seconds");
            }
            else{
                redisTemplate.opsForValue().increment(email1+"_createPayment");
            }
        }
        else{
            redisTemplate.opsForValue().increment(email1+"_createPayment");
            redisTemplate.expire(email1+"_createPayment",60L, TimeUnit.SECONDS);
        }


        //checking if value is negative
        if(paymentDTO.getAmountPaid().compareTo(BigDecimal.ZERO)<0){
            throw new InvalidInputException("Amount less than","0");
        }

        //method logic
        User user1= userService.getUser(email1);
        User user2= userService.getUser(email2);
        Payment payment= this.paymentDTOToPayment(paymentDTO);
        try{
            //saving payment object
            payment.setPaidBy(user1);
            payment.setPaidTo(user2);
            payment.setCreatedAt(LocalDate.now());
            paymentRepository.save(payment);
            //updating balance
            Boolean settled= balanceService.updatePaymentBalance(user1,user2,paymentDTO.getAmountPaid(),paymentDTO.getCurrency());
            if(settled){
                expenseService.settleExpenses(user1,user2);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new TransactionFailedException("failed to save payment record");
        }
    }

    @Override
    public Payment paymentDTOToPayment(PaymentDTO paymentDTO) {
        return modelMapper.map(paymentDTO,Payment.class);
    }

    @Override
    public PaymentDTO paymentToPaymentDTO(Payment payment) {
        return modelMapper.map(payment,PaymentDTO.class);
    }

    @Override
    public List<PaymentResponseDTO> getPayments(String email1, String email2) {
        User user1= userService.getUser(email1);
        User user2= userService.getUser(email2);
        List<Payment> payments = paymentRepository.findByPaidByAndPaidToOrPaidByAndPaidTo(user1,user2,user2,user1);
        return payments.stream().map((payment -> this.paymentToPaymentResponseDTO(payment))).toList();
    }

    @Override
    public PaymentResponseDTO paymentToPaymentResponseDTO(Payment payment) {
        return modelMapper.map(payment,PaymentResponseDTO.class);
    }

}
