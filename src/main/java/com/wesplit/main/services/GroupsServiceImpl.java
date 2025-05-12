package com.wesplit.main.services;

import com.wesplit.main.entities.Groups;
import com.wesplit.main.entities.User;
import com.wesplit.main.payloads.GroupDTO;
import com.wesplit.main.repositories.GroupsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class GroupsServiceImpl implements GroupsService{
    private final UserService userService;
    private final GroupsRepository groupsRepository;
    private final ModelMapper modelMapper;

    GroupsServiceImpl(GroupsRepository groupsRepository, UserService userService, ModelMapper modelMapper){
        this.groupsRepository=groupsRepository;
        this.userService=userService;
        this.modelMapper = modelMapper;
    }
    @Override
    public GroupDTO addGroup(String email,String groupName) {
        List<User> members=new ArrayList<>();
        User user= userService.getUser(email);
        members.add(user);
        Groups groups= Groups.builder()
                .createdAt(LocalDate.now())
                .groupName(groupName)
                .members(members)
                .build();
        groupsRepository.save(groups);
        return this.groupToGroupDTO(groups);
    }

    @Override
    public GroupDTO groupToGroupDTO(Groups groups) {
        return modelMapper.map(groups,GroupDTO.class);
    }
}
