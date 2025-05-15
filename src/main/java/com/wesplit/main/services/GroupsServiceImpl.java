package com.wesplit.main.services;

import com.wesplit.main.entities.Groups;
import com.wesplit.main.entities.User;
import com.wesplit.main.exceptions.ResourceAlreadyExistsException;
import com.wesplit.main.exceptions.ResourceNotFoundException;
import com.wesplit.main.exceptions.TransactionFailedException;
import com.wesplit.main.payloads.FriendDTO;
import com.wesplit.main.payloads.GroupDTO;
import com.wesplit.main.repositories.GroupsRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GroupsServiceImpl implements GroupsService{
    private final UserService userService;
    private final GroupsRepository groupsRepository;
    private final ModelMapper modelMapper;
    private final BalanceService balanceService;

    GroupsServiceImpl(GroupsRepository groupsRepository, UserService userService, ModelMapper modelMapper, BalanceService balanceService){
        this.groupsRepository=groupsRepository;
        this.userService=userService;
        this.modelMapper = modelMapper;
        this.balanceService = balanceService;
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

    @Override
    public List<FriendDTO> addMembers(List<String> emails,Long groupId) {
        Groups groups= groupsRepository.findById(groupId).orElseThrow(()-> new ResourceNotFoundException("Group",groupId+""));
        List<User> members=groups.getMembers();
        List<User> newMembers= emails.stream().map(userService::getUser).toList();
        //checking if the member already exists
        for (User member : members) {
            for (User newMember : newMembers) {
                if (member.getUserId().equals(newMember.getUserId())) {
                    throw new ResourceAlreadyExistsException("Member",member.getFirstName()+"");
                }
            }
        }
        //creating balances
        this.createBalances(members,newMembers,groupId);
        members.addAll(newMembers);
        groups.setMembers(members);
        try{
            groupsRepository.save(groups);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new TransactionFailedException("failed to add members");
        }

        return members.stream().map(userService::userToFriendDTO).toList();
    }

    //helper method
    @Override
    public void createBalances(List<User> members, List<User> newMembers,Long groupId) {
        //creating balances of old members with newMembers
        for (User member : members) {
            for (User newMember : newMembers) {
                balanceService.addNewBalance(member,newMember,groupId);
            }
        }
        //creating balances between new members
        for(int i=0;i<newMembers.size()-1;i++){
            for(int j=i+1;j<newMembers.size();j++){
                balanceService.addNewBalance(newMembers.get(i),newMembers.get(j),groupId);
            }
        }
    }

    @Override
    public List<GroupDTO> getGroups(String email) {
        User user= userService.getUser(email);
        List<Groups> groups=groupsRepository.findAllByMembers(user);
        return groups.stream().map(this::groupToGroupDTO).toList();
    }
}
