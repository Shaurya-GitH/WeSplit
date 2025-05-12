package com.wesplit.main.services;

import com.wesplit.main.entities.User;
import com.wesplit.main.payloads.FriendDTO;
import com.wesplit.main.repositories.FriendListRepository;
import com.wesplit.main.utils.RedisUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FriendListServiceImpl implements FriendListService{
    private final RedisUtil redisUtil;
    FriendListRepository friendListRepository;
    UserService userService;
    FriendListServiceImpl(FriendListRepository friendListRepository, UserService userService, RedisUtil redisUtil){
        this.friendListRepository=friendListRepository;
        this.userService=userService;
        this.redisUtil = redisUtil;
    }
    @Transactional
    @Override
    public List<FriendDTO> getAllFriends(String email) {
        List<FriendDTO> cache= redisUtil.getListValue(email+"_friends",FriendDTO.class);
        if(cache!=null){
            return cache;
        }
        else{
            User user= userService.getUser(email);
            List<FriendDTO> list=friendListRepository.getByUser(user) ;
            redisUtil.setValue(email+"_friends",list,300L);
            return list;
        }
    }
}
