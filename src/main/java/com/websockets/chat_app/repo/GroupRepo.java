package com.websockets.chat_app.repo;

import com.websockets.chat_app.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepo extends JpaRepository<Group, Integer> {
    List<Group> findByMembersUsername(String username);
}
