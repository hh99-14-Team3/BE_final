package com.mogakko.be_final.domain.friendship.repository;

import com.mogakko.be_final.domain.friendship.entity.Friendship;
import com.mogakko.be_final.domain.friendship.entity.FriendshipStatus;
import com.mogakko.be_final.domain.members.entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    Optional<Friendship> findAllBySenderAndReceiver(Members sender, Members receiver);

    Optional<Friendship> findAllBySenderOrReceiver(Members sender, Members receiver);

    Friendship findBySenderAndReceiverAndStatus(Members sender, Members receiver, FriendshipStatus status);

    List<Friendship> findAllBySenderOrReceiverAndStatus(Members sender, Members receiver, FriendshipStatus status);

    List<Friendship> findAllByReceiverAndStatus(Members receiver, FriendshipStatus status);
}
