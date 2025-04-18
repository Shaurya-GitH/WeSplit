package com.wesplit.main.controllers;

import com.wesplit.main.entities.User;
import com.wesplit.main.payloads.BalanceDTO;
import com.wesplit.main.services.BalanceService;
import com.wesplit.main.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/balance")
public class BalanceController {

    private final BalanceService balanceService;
    private final UserService userService;

    public BalanceController(BalanceService balanceService, UserService userService) {
        this.balanceService = balanceService;
        this.userService = userService;
    }

    //API getBalance
    @GetMapping("/{user2Email}")
    ResponseEntity<BalanceDTO> getBalance(@PathVariable String user2Email){
        String user1Email= SecurityContextHolder.getContext().getAuthentication().getName();
        User user1= userService.getUser(user1Email);
        User user2= userService.getUser(user2Email);
        BalanceDTO balance= balanceService.getBalance(user1,user2);
        return ResponseEntity.ok().body(balance);
    }
}
