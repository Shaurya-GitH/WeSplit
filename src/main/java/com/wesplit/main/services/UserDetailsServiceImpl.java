package com.wesplit.main.services;

import com.wesplit.main.exceptions.ResourceNotFoundException;
import com.wesplit.main.repositories.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    UserRepository userRepository;
    UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository=userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
           com.wesplit.main.entities.User user =userRepository.findByEmailAndRegisterStatusTrue(username).orElseThrow(()->new ResourceNotFoundException("Email",username+""));
           String email=user.getEmail();
           String password=user.getPassword();
           List<String> rolesList=user.getRoles();
           String[] arr= rolesList.toArray(new String[0]);
        System.out.println("Roles: " + Arrays.toString(arr));

        return User.builder()
                   .username(email)
                   .password(password)
                   .roles(arr)
                   .build();
    }
}


