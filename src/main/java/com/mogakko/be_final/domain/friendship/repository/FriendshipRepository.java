package com.mogakko.be_final.domain.friendship.repository;

import com.mogakko.be_final.domain.friendship.entity.Friendship;
import com.mogakko.be_final.domain.members.entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findAllBySenderOrReceiver(Members sender, Members receiver);
}
