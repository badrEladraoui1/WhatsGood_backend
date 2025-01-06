package com.websockets.chat_app.service.impl;

import com.websockets.chat_app.entity.Group;
import com.websockets.chat_app.entity.User;
import com.websockets.chat_app.repo.GroupRepo;
import com.websockets.chat_app.service.GroupService;
import com.websockets.chat_app.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GroupServiceImpl implements GroupService {
    private final GroupRepo groupRepo;
    private final UserService userService;

    public GroupServiceImpl(GroupRepo groupRepo, UserService userService) {
        this.groupRepo = groupRepo;
        this.userService = userService;
    }

    @Override
    @Transactional
    public Group createGroup(String name, String description, String creatorUsername, List<String> memberUsernames) {
        Group group = new Group(name, description, creatorUsername);

        // Add creator as a member
        User creator = userService.findByUsername(creatorUsername)
                .orElseThrow(() -> new RuntimeException("Creator not found"));
        group.addMember(creator);

        // Add other members
        memberUsernames.stream()
                .map(username -> userService.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found: " + username)))
                .forEach(group::addMember);

        return groupRepo.save(group);
    }

    @Override
    @Transactional
    public void addMemberToGroup(Integer groupId, String username) {
        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        group.addMember(user);
        groupRepo.save(group);
    }

    @Override
    @Transactional
    public void removeMemberFromGroup(Integer groupId, String username) {
        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        group.removeMember(user);
        groupRepo.save(group);
    }

    @Override
    public List<Group> getUserGroups(String username) {
        return groupRepo.findByMembersUsername(username);
    }

//    @Override
//    public Optional<Group> getGroupById(Integer groupId) {
//        return groupRepo.findById(groupId);
//    }

    @Override
    public Optional<Group> getGroupById(Integer groupId) {
        return groupRepo.findById(groupId)
                .map(group -> {
                    // Force initialization of members
                    group.getMembers().size();
                    return group;
                });
    }
}