package com.wesplit.main.controllers;

import com.wesplit.main.entities.User;
import com.wesplit.main.payloads.BalanceDTO;
import com.wesplit.main.services.BalanceService;
import com.wesplit.main.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping("solo/{user2Email}")
    ResponseEntity<BalanceDTO> getBalance(@PathVariable("user2Email") String user2Email, @CookieValue(value = "currency",defaultValue = "INR") String currency){
        String user1Email= SecurityContextHolder.getContext().getAuthentication().getName();
        User user1= userService.getUser(user1Email);
        User user2= userService.getUser(user2Email);
        BalanceDTO balance= balanceService.getBalance(user1,user2,currency);
        return ResponseEntity.ok().body(balance);
    }

    //API getGroupBalance
    @GetMapping("group/{groupId}")
    ResponseEntity<List<BalanceDTO>> getGroupBalance(@PathVariable("groupId") Long groupId){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        User user=userService.getUser(authentication.getName());
        List<BalanceDTO> list= balanceService.getGroupBalance(user,groupId);
        return ResponseEntity.ok().body(list);
    }
}
