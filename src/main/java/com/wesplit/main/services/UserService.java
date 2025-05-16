package com.wesplit.main.services;

import com.wesplit.main.entities.User;
import com.wesplit.main.payloads.FriendDTO;
import com.wesplit.main.payloads.UserDTO;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    FriendDTO registerUser(UserDTO userDTO);
    FriendDTO addFriend(FriendDTO userDTO, String email);
    User userDtoToUser(UserDTO userDTO);
    UserDTO userToUserDto(User user);
    User getUser(String userEmail);
    User friendDTOtoUser(FriendDTO friendDTO);
    FriendDTO userToFriendDTO(User user);
}
