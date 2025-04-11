package com.wesplit.main.services;

import com.wesplit.main.entities.FriendList;
import com.wesplit.main.entities.User;
import com.wesplit.main.payloads.FriendDTO;
import com.wesplit.main.repositories.FriendListRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendListServiceImpl implements FriendListService{
    FriendListRepository friendListRepository;
    UserService userService;
    FriendListServiceImpl(FriendListRepository friendListRepository, UserService userService){
        this.friendListRepository=friendListRepository;
        this.userService=userService;
    }
    @Override
    public List<FriendDTO> getAllFriends(String email) {
        User user= userService.getUser(email);
        FriendList friendList=friendListRepository.findByUser(user).get();
        List<FriendDTO> list= friendList.getFriends().stream().map(friend ->userService.userToFriendDTO(friend)).toList();
        return list;
    }
}
