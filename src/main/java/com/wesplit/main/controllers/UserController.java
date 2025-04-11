package com.wesplit.main.controllers;

import com.wesplit.main.entities.User;
import com.wesplit.main.payloads.FriendDTO;
import com.wesplit.main.payloads.UserDTO;
import com.wesplit.main.repositories.FriendListRepository;
import com.wesplit.main.services.FriendListService;
import com.wesplit.main.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final FriendListService friendListService;
    UserController(UserService userService, FriendListService friendListService){
        this.userService=userService;
        this.friendListService=friendListService;
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

    //API getFriends
    @GetMapping("/friend-list")
    ResponseEntity<List<FriendDTO>> getFriends(){
       Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
      List<FriendDTO> friends= friendListService.getAllFriends(authentication.getName());
        return ResponseEntity.ok().body(friends);
    }

    //API authCheck
    @GetMapping("/auth/check")
    ResponseEntity<?> checkLogin(){
        return ResponseEntity.ok().build();
    }
}
