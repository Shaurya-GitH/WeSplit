package com.wesplit.main.controllers;

import com.wesplit.main.payloads.ExpenseDTO;
import com.wesplit.main.payloads.ExpenseResponseDTO;
import com.wesplit.main.payloads.ExpenseSplitDTO;
import com.wesplit.main.payloads.GroupExpenseDTO;
import com.wesplit.main.services.ExpenseService;
import com.wesplit.main.services.ExpenseSplitService;
import com.wesplit.main.services.GroupExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expense")
public class ExpenseController {
    private final ExpenseSplitService expenseSplitService;
    private final ExpenseService expenseService;
    private final GroupExpenseService groupExpenseService;

    @Autowired
    ExpenseController(ExpenseService expenseService, ExpenseSplitService expenseSplitService, GroupExpenseService groupExpenseService){
        this.expenseService=expenseService;
        this.expenseSplitService = expenseSplitService;
        this.groupExpenseService = groupExpenseService;
    }
    //API addExpense
    @PostMapping("/create-solo/{user2Email}")
    ResponseEntity<ExpenseResponseDTO> createExpense(@Valid @RequestBody ExpenseDTO expenseDTO, @PathVariable("user2Email") String user2Email){
       Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
       ExpenseResponseDTO newExpense= expenseService.createExpense(expenseDTO,authentication.getName(),user2Email);
        return ResponseEntity.status(HttpStatus.CREATED).body(newExpense);
    }

    //API getUnsettledExpenses
    @GetMapping("/unsettled/{user2Email}")
    ResponseEntity<List<ExpenseResponseDTO>> getUnsettledExpenses(@PathVariable("user2Email") String user2Email){
       Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
       List<ExpenseResponseDTO> list= expenseService.getUnsettledExpenses(authentication.getName(),user2Email);
       return ResponseEntity.ok().body(list);
    }

    //API getSettledExpenses
    @GetMapping("/settled/{userEmail2}")
    ResponseEntity<List<ExpenseResponseDTO>> getSettledExpenses(@PathVariable("userEmail2") String userEmail2){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        List<ExpenseResponseDTO> list= expenseService.getSettledExpenses(authentication.getName(),userEmail2);
        return ResponseEntity.ok().body(list);
    }

    //API getExpenseSplits
    @GetMapping("/splits/{expenseId}")
    ResponseEntity<List<ExpenseSplitDTO>> getExpenseSplits(@PathVariable("expenseId") Long expenseId){
        List<ExpenseSplitDTO> list=expenseSplitService.getExpenseSplits(expenseId);
        return ResponseEntity.ok().body(list);
    }

    //API addGroupExpense
    @PostMapping("/create-group")
    ResponseEntity<?> addGroupExpense(@Valid @RequestBody GroupExpenseDTO groupExpenseDTO){
        ExpenseResponseDTO expenseResponseDTO=groupExpenseService.createGroupExpense(groupExpenseDTO);
        return ResponseEntity.ok().body(expenseResponseDTO);
    }
}
