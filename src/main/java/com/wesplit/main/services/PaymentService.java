package com.wesplit.main.services;

import com.wesplit.main.entities.Payment;
import com.wesplit.main.payloads.PaymentDTO;
import com.wesplit.main.payloads.PaymentResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PaymentService {
    void createPayment(PaymentDTO paymentDTO);
    Payment paymentDTOToPayment(PaymentDTO paymentDTO);
    PaymentDTO paymentToPaymentDTO(Payment payment);
    List<PaymentResponseDTO> getPayments(String email1, String email2);
    List<PaymentResponseDTO> getGroupPayments(Long groupId);
    PaymentResponseDTO paymentToPaymentResponseDTO(Payment payment);
    void createGroupPayment(PaymentDTO paymentDTO);
    void paymentCheck(PaymentDTO paymentDTO);
}
