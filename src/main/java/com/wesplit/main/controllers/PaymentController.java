package com.wesplit.main.controllers;

import com.wesplit.main.payloads.PaymentDTO;
import com.wesplit.main.payloads.PaymentResponseDTO;
import com.wesplit.main.services.BalanceService;
import com.wesplit.main.services.GroupExpenseService;
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
    private final BalanceService balanceService;
    private final GroupExpenseService groupExpenseService;

    PaymentController(PaymentService paymentService, BalanceService balanceService, GroupExpenseService groupExpenseService){
        this.paymentService=paymentService;
        this.balanceService = balanceService;
        this.groupExpenseService = groupExpenseService;
    }
    //API createPayment
    @PostMapping("/create")
    ResponseEntity<?> createPayment(@Valid @RequestBody PaymentDTO paymentDTO){
        paymentService.createPayment(paymentDTO);
        return ResponseEntity.ok().build();
    }

    //API getPayments
    @GetMapping("/{user2Email}")
    ResponseEntity<List<PaymentResponseDTO>> getAllPayments(@PathVariable("user2Email") String user2Email){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        List<PaymentResponseDTO> list= paymentService.getPayments(authentication.getName(),user2Email);
        return ResponseEntity.ok().body(list);
    }

    //API getGroupPayments
    @GetMapping("/group/{groupId}")
    ResponseEntity<List<PaymentResponseDTO>> getAllGroupPayments(@PathVariable("groupId") Long groupId){
        List<PaymentResponseDTO> list= paymentService.getGroupPayments(groupId);
        return ResponseEntity.ok().body(list);
    }

    //API createGroupPayment
    @PostMapping("/createGroup")
    ResponseEntity<?> createGroupPayment(@Valid @RequestBody PaymentDTO paymentDTO){
        paymentService.createGroupPayment(paymentDTO);
        Boolean settled= balanceService.groupBalanceSettledCheck(paymentDTO.getGroupId());
        if(settled){
            groupExpenseService.settleGroupExpenses(paymentDTO.getGroupId());
        }
        return ResponseEntity.ok().build();
    }
}
