package com.wesplit.main.controllers;

import com.wesplit.main.payloads.GroupDTO;
import com.wesplit.main.services.GroupsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/groups")
public class GroupsController {

    private final GroupsService groupsService;
    GroupsController(GroupsService groupsService){
        this.groupsService=groupsService;
    }

    @PostMapping("/add/{groupName}")
    ResponseEntity<GroupDTO> addGroup(@PathVariable("groupName")String groupName){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String email= authentication.getName();
        GroupDTO groupDTO= groupsService.addGroup(email,groupName);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupDTO);
    }
}
