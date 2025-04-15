package com.wesplit.main.controllers;

import com.wesplit.main.payloads.LoginUser;
import com.wesplit.main.payloads.UserDTO;
import com.wesplit.main.services.UserService;
import com.wesplit.main.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Autowired
    PublicController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserDetailsService userDetailsService){
        this.userService=userService;
        this.authenticationManager=authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    //API registerUser
    @PostMapping("/register")
    ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserDTO userDTO){
        UserDTO newUser= userService.registerUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    //API login
    @PostMapping("/login")
    ResponseEntity<String> authenticateUser(@Valid @RequestBody LoginUser loginUser){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getEmail(),loginUser.getPassword()));
        UserDetails userDetails=userDetailsService.loadUserByUsername(loginUser.getEmail());
        String token= jwtUtil.generateToken(userDetails.getUsername());
        return ResponseEntity.ok().body(token);
    }
}
