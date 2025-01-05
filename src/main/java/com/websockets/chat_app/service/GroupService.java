package com.websockets.chat_app.service;

import com.websockets.chat_app.entity.Group;

import java.util.List;
import java.util.Optional;

public interface GroupService {
    Group createGroup(String name, String description, String creatorUsername, List<String> memberUsernames);
    void addMemberToGroup(Integer groupId, String username);
    void removeMemberFromGroup(Integer groupId, String username);
    List<Group> getUserGroups(String username);
    Optional<Group> getGroupById(Integer groupId);
}
