package com.wesplit.main.services;

import com.wesplit.main.entities.Payment;
import com.wesplit.main.entities.User;
import com.wesplit.main.exceptions.TransactionFailedException;
import com.wesplit.main.payloads.PaymentDTO;
import com.wesplit.main.payloads.PaymentResponseDTO;
import com.wesplit.main.repositories.PaymentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService{
    private final BalanceService balanceService;
    private final ExpenseService expenseService;
    PaymentRepository paymentRepository;
    UserService userService;
    ModelMapper modelMapper;
    PaymentServiceImpl(PaymentRepository paymentRepository, UserService userService, ModelMapper modelMapper, BalanceService balanceService, ExpenseService expenseService){
        this.paymentRepository=paymentRepository;
        this.userService=userService;
        this.modelMapper=modelMapper;
        this.balanceService = balanceService;
        this.expenseService = expenseService;
    }
    @Transactional
    @Override
    public void createPayment(PaymentDTO paymentDTO, String email1, String email2) {
        User user1= userService.getUser(email1);
        User user2= userService.getUser(email2);
        Payment payment= this.paymentDTOToPayment(paymentDTO);
        try{
            //saving payment object
            payment.setPaidBy(user1);
            payment.setPaidTo(user2);
            paymentRepository.save(payment);
            //updating balance
            Boolean settled= balanceService.updatePaymentBalance(user1,user2,paymentDTO.getAmountPaid());
            if(settled){
                expenseService.settleExpenses(user1,user2);
            }
        } catch (Exception e) {
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
