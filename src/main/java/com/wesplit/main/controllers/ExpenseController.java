package com.wesplit.main.controllers;

import com.wesplit.main.payloads.ExpenseDTO;
import com.wesplit.main.payloads.ExpenseResponseDTO;
import com.wesplit.main.services.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/expense")
public class ExpenseController {
    private ExpenseService expenseService;
    @Autowired
    ExpenseController(ExpenseService expenseService){
        this.expenseService=expenseService;
    }
    //API addExpense
    @PostMapping("/create-solo/{user2}")
    ResponseEntity<ExpenseResponseDTO> createExpense(@Valid @RequestBody ExpenseDTO expenseDTO, @PathVariable String user2){
       Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
       ExpenseResponseDTO newExpense= expenseService.createExpense(expenseDTO,authentication.getName(),user2);
        return ResponseEntity.status(HttpStatus.CREATED).body(newExpense);
    }
}
