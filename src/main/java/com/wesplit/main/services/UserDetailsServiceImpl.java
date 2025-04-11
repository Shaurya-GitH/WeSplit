package com.wesplit.main.services;

import com.wesplit.main.exceptions.ResourceNotFoundException;
import com.wesplit.main.repositories.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    UserRepository userRepository;
    UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository=userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
           String user=userRepository.findByEmail(username).orElseThrow(()->new ResourceNotFoundException("Email",username+"")).getEmail();
           String password=userRepository.findByEmail(username).orElseThrow(()->new ResourceNotFoundException("Email",username+"")).getPassword();
           return User.builder()
                   .username(user)
                   .password(password)
                   .build();
    }
}

