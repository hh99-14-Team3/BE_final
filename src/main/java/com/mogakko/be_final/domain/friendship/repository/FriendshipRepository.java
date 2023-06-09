package com.mogakko.be_final.domain.friendship.repository;

import com.mogakko.be_final.domain.friendship.entity.Friendship;
import com.mogakko.be_final.domain.friendship.entity.FriendshipStatus;
import com.mogakko.be_final.domain.members.entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    Optional<Friendship> findBySenderAndReceiver(Members sender, Members receiver);

    Optional<Friendship> findBySenderAndReceiverAndStatus(Members sender, Members receiver, FriendshipStatus status);

    List<Friendship> findAllByReceiverAndStatusOrSenderAndStatus(Members receiver, FriendshipStatus status1, Members sender, FriendshipStatus status2);

    List<Friendship> findAllByReceiverAndStatus(Members receiver, FriendshipStatus status);

    void deleteAllBySenderAndReceiver(Members sender, Members receiver);
}
