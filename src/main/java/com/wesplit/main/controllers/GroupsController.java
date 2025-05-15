package com.wesplit.main.controllers;

import com.wesplit.main.payloads.FriendDTO;
import com.wesplit.main.payloads.GroupDTO;
import com.wesplit.main.services.GroupsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/groups")
public class GroupsController {

    private final GroupsService groupsService;
    GroupsController(GroupsService groupsService){
        this.groupsService=groupsService;
    }

    //API createGroup
    @PostMapping("/create/{groupName}")
    ResponseEntity<GroupDTO> addGroup(@PathVariable("groupName")String groupName){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String email= authentication.getName();
        GroupDTO groupDTO= groupsService.addGroup(email,groupName);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupDTO);
    }

    //API addMember
    @PostMapping("/add/{groupId}")
    ResponseEntity<?> addMember(@RequestBody List<String> emails,@PathVariable("groupId") Long groupId){
        List<FriendDTO> members= groupsService.addMembers(emails,groupId);
        return ResponseEntity.status(HttpStatus.OK).body(members);
    }

    //API getGroups
    @GetMapping("/")
    ResponseEntity<List<GroupDTO>> getGroups(){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        List<GroupDTO> groups=groupsService.getGroups(authentication.getName());
        return ResponseEntity.ok().body(groups);
    }
}
