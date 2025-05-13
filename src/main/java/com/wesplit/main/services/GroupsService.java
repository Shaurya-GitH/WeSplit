package com.wesplit.main.services;

import com.wesplit.main.entities.Groups;
import com.wesplit.main.entities.User;
import com.wesplit.main.payloads.FriendDTO;
import com.wesplit.main.payloads.GroupDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GroupsService {
    GroupDTO addGroup(String email,String groupName);
    GroupDTO groupToGroupDTO(Groups groups);
    List<FriendDTO> addMembers(List<String> emails,Long groupId);
    void createBalances(List<User> members,List<User> newMembers,Long groupId);
}
