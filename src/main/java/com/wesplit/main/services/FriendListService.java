package com.wesplit.main.services;

import com.wesplit.main.payloads.FriendDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FriendListService {
    List<FriendDTO> getAllFriends(String email);
}
