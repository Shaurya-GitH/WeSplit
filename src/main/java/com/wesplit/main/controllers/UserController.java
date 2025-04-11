package com.wesplit.main.controllers;

import com.wesplit.main.payloads.FriendDTO;
import com.wesplit.main.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService userService;
    UserController(UserService userService){
        this.userService=userService;
    }

    //API addFriend
    @PostMapping("/add-friend")
    ResponseEntity<FriendDTO> addFriend(@Valid @RequestBody FriendDTO friendDTO){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        FriendDTO newFriend= userService.addFriend(friendDTO,authentication.getName());
        if(newFriend!=null){
            //if new record is created for friend
            return ResponseEntity.status(HttpStatus.CREATED).body(newFriend);
        }
        else{
            //if friend is already in the user record
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }
}
