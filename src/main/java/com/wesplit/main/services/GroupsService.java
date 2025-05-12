package com.wesplit.main.services;

import com.wesplit.main.entities.Groups;
import com.wesplit.main.payloads.GroupDTO;
import org.springframework.stereotype.Service;

@Service
public interface GroupsService {
    GroupDTO addGroup(String email,String groupName);
    GroupDTO groupToGroupDTO(Groups groups);
}
