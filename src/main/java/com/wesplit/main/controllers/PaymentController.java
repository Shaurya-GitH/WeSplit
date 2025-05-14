package com.wesplit.main.controllers;

import com.wesplit.main.payloads.PaymentDTO;
import com.wesplit.main.payloads.PaymentResponseDTO;
import com.wesplit.main.services.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    final private PaymentService paymentService;
    PaymentController(PaymentService paymentService){
        this.paymentService=paymentService;
    }
    //API createPayment
    @PostMapping("/create/{user2Email}")
    ResponseEntity<?> createPayment(@Valid @RequestBody PaymentDTO paymentDTO, @PathVariable("user2Email") String user2Email){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        paymentService.createPayment(paymentDTO, authentication.getName(),user2Email);
        return ResponseEntity.ok().build();
    }

    //API getPayments
    @GetMapping("/{user2Email}")
    ResponseEntity<List<PaymentResponseDTO>> getAllPayments(@PathVariable("user2Email") String user2Email){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        List<PaymentResponseDTO> list= paymentService.getPayments(authentication.getName(),user2Email);
        return ResponseEntity.ok().body(list);
    }
}
