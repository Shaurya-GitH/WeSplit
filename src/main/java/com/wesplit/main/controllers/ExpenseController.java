package com.wesplit.main.controllers;

import com.wesplit.main.payloads.ExpenseDTO;
import com.wesplit.main.payloads.ExpenseResponseDTO;
import com.wesplit.main.payloads.ExpenseSplitDTO;
import com.wesplit.main.services.ExpenseService;
import com.wesplit.main.services.ExpenseSplitService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expense")
public class ExpenseController {
    private final ExpenseSplitService expenseSplitService;
    private ExpenseService expenseService;
    @Autowired
    ExpenseController(ExpenseService expenseService, ExpenseSplitService expenseSplitService){
        this.expenseService=expenseService;
        this.expenseSplitService = expenseSplitService;
    }
    //API addExpense
    @PostMapping("/create-solo/{user2}")
    ResponseEntity<ExpenseResponseDTO> createExpense(@Valid @RequestBody ExpenseDTO expenseDTO, @PathVariable String user2){
       Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
       ExpenseResponseDTO newExpense= expenseService.createExpense(expenseDTO,authentication.getName(),user2);
        return ResponseEntity.status(HttpStatus.CREATED).body(newExpense);
    }

    //API getExpenses
    @GetMapping("/{userEmail2}")
    ResponseEntity<List<ExpenseResponseDTO>> getExpenses(@PathVariable String userEmail2){
       Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
       List<ExpenseResponseDTO> list= expenseService.getExpenses(authentication.getName(),userEmail2);
       return ResponseEntity.ok().body(list);
    }

    //API getExpenseSplits
    @GetMapping("/splits/{expenseId}")
    ResponseEntity<List<ExpenseSplitDTO>> getExpenseSplits(@PathVariable Long expenseId){
        List<ExpenseSplitDTO> list=expenseSplitService.getExpenseSplits(expenseId);
        return ResponseEntity.ok().body(list);
    }
}
