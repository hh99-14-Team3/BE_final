package com.mogakko.be_final.domain.friendship.repository;

import com.mogakko.be_final.domain.friendship.entity.Friendship;
import com.mogakko.be_final.domain.friendship.entity.FriendshipStatus;
import com.mogakko.be_final.domain.members.entity.Members;
import jnr.a64asm.OP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    Optional<Friendship> findBySenderAndReceiver(Members sender, Members receiver);

    Optional<Friendship> findBySenderOrReceiver(Members sender, Members receiver);

    Optional<Friendship> findBySenderAndReceiverAndStatus(Members sender, Members receiver, FriendshipStatus status);

    List<Friendship> findAllBySenderOrReceiverAndStatus(Members sender, Members receiver, FriendshipStatus status);

    List<Friendship> findAllBySenderAndStatus(Members sender,FriendshipStatus status);

    List<Friendship> findAllByReceiverAndStatus(Members receiver, FriendshipStatus status);
}
