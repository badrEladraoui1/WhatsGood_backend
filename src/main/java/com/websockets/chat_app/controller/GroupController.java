package com.websockets.chat_app.controller;

import com.websockets.chat_app.entity.Group;
import com.websockets.chat_app.service.GroupService;
import com.websockets.chat_app.service.impl.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class GroupController {
    private final GroupService groupService;
    private final JwtService jwtService;

    public GroupController(GroupService groupService, JwtService jwtService) {
        this.groupService = groupService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<Group> createGroup(
            @RequestBody CreateGroupRequest request,
            @RequestHeader("Authorization") String token) {
        String username = jwtService.getUsernameFromToken(token.replace("Bearer ", ""));
        Group group = groupService.createGroup(
                request.getName(),
                request.getDescription(),
                username,
                request.getMembers()
        );
        return ResponseEntity.ok(group);
    }

    @GetMapping
    public ResponseEntity<List<Group>> getUserGroups(
            @RequestHeader("Authorization") String token) {
        String username = jwtService.getUsernameFromToken(token.replace("Bearer ", ""));
        return ResponseEntity.ok(groupService.getUserGroups(username));
    }
}

class CreateGroupRequest {
    private String name;
    private String description;
    private List<String> members;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getMembers() { return members; }
    public void setMembers(List<String> members) { this.members = members; }
}