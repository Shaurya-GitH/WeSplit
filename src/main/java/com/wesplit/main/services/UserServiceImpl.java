package com.wesplit.main.services;

import com.wesplit.main.entities.FriendList;
import com.wesplit.main.entities.User;
import com.wesplit.main.exceptions.InvalidInputException;
import com.wesplit.main.exceptions.ResourceAlreadyExistsException;
import com.wesplit.main.exceptions.ResourceNotFoundException;
import com.wesplit.main.exceptions.TransactionFailedException;
import com.wesplit.main.payloads.FriendDTO;
import com.wesplit.main.payloads.UserDTO;
import com.wesplit.main.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    final private UserRepository userRepository;
    final private ModelMapper modelMapper;
    final private BalanceService balanceService;
    final private PasswordEncoder passwordEncoder;
    @Autowired
    UserServiceImpl(UserRepository userRepository,ModelMapper modelMapper,BalanceService balanceService,PasswordEncoder passwordEncoder){
        this.userRepository=userRepository;
        this.modelMapper=modelMapper;
        this.balanceService=balanceService;
        this.passwordEncoder=passwordEncoder;
    }

    @Override
    public FriendDTO registerUser(UserDTO userDTO) {

        try{
            User user=this.userDtoToUser(userDTO);
            //creating a friend-list for the new user
            FriendList friendList=FriendList.builder()
                                 .user(user)
                                 .friends(new ArrayList<>())
                                 .build();
            user.setFriendList(friendList);
            String hashPw= passwordEncoder.encode(user.getPassword());
            user.setPassword(hashPw);
            user.setRegisterStatus(true);
            //setting role as USER
            List<String> list=new ArrayList<>();
            list.add("USER");
            user.setRoles(list);
            User newUser= userRepository.save(user);
            return this.userToFriendDTO(newUser);
        }
        //if user is already in the record
        catch (DataIntegrityViolationException e){
            User user=this.userDtoToUser(userDTO);
            User friendUser= userRepository.findByEmail(user.getEmail()).get();
            if(friendUser.isRegisterStatus()){
                //if user is already registered
                throw new ResourceAlreadyExistsException("Account with email: ",user.getEmail());
            }
            else{
                //creating a friend-list for the new user
                FriendList friendList=FriendList.builder()
                        .user(friendUser)
                        .friends(new ArrayList<>())
                        .build();
                //setting role as USER
                List<String> list=new ArrayList<>();
                list.add("USER");
                friendUser.setRoles(list);
                String hashPw= passwordEncoder.encode(user.getPassword());
                friendUser.setPassword(hashPw);
                friendUser.setFriendList(friendList);
                friendUser.setRegisterStatus(true);
                friendUser.setFirstName(user.getFirstName());
                friendUser.setLastName(user.getLastName());
                try{
                    User newUser= userRepository.save(friendUser);
                    return this.userToFriendDTO(newUser);
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                    throw new TransactionFailedException("Failed to save friend as a user");
                }
            }
        }
    }

    @Transactional
    @Override
    public FriendDTO addFriend(FriendDTO friendDTO,String email) {
        User user=this.friendDTOtoUser(friendDTO);
        Optional<User> friendUser= userRepository.findByEmail(user.getEmail());
        User loggedUser=userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User","userId"));
        //if friend user is already in the record
        if(friendUser.isPresent()){
            //check if friend is not the user itself
            if(friendUser.get().getEmail().equals(email)){
                throw new InvalidInputException("friend as","yourself");
            }
            //check if friend is already in the friend-list
            List<User> friendList= loggedUser.getFriendList().getFriends();
            if(friendList.contains(friendUser.get())){
                throw new ResourceAlreadyExistsException("Friend",user.getFirstName());
            }
            else{
                //creating a balance record between the friends
                balanceService.addNewBalance(friendUser.get(),loggedUser,null);
                friendList.add(friendUser.get());
                FriendList newList= loggedUser.getFriendList();
                newList.setFriends(friendList);
                loggedUser.setFriendList(newList);
                userRepository.save(loggedUser);
                return null;
            }
        }
        //if friend user is not in the record
        else{
            //putting the friend user in the record
            user.setRegisterStatus(false);
            user.setPassword(null);
            user.setFriendList(null);
            try {
                User newUser = userRepository.save(user);
                //creating a balance record between the friends
                balanceService.addNewBalance(loggedUser, newUser,null);
                //adding the friend to the friend-list
                List<User> friendList = loggedUser.getFriendList().getFriends();
                friendList.add(newUser);
                FriendList newList = loggedUser.getFriendList();
                newList.setFriends(friendList);
                loggedUser.setFriendList(newList);
                userRepository.save(loggedUser);
                return this.userToFriendDTO(newUser);
            }
            catch (Exception e){
                log.error(e.getMessage());
                throw new TransactionFailedException("Failed to add friend");
            }
        }
    }

    @Override
    public User userDtoToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO,User.class);
    }

    @Override
    public UserDTO userToUserDto(User user) {
        return modelMapper.map(user,UserDTO.class);
    }

    @Override
    public User getUser(String userEmail) {
        //get the reference to the user
        return userRepository.findByEmail(userEmail).orElseThrow(()->new ResourceNotFoundException("User",userEmail+""));
    }

    @Override
    public User friendDTOtoUser(FriendDTO friendDTO) {
        return modelMapper.map(friendDTO,User.class);
    }

    @Override
    public FriendDTO userToFriendDTO(User user) {
        return modelMapper.map(user,FriendDTO.class);
    }

}
