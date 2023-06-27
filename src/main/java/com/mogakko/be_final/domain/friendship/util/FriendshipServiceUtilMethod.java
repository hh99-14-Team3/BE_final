package com.mogakko.be_final.domain.friendship.util;

import com.mogakko.be_final.domain.friendship.entity.FriendshipStatus;
import com.mogakko.be_final.domain.friendship.repository.FriendshipRepository;
import com.mogakko.be_final.domain.members.entity.Members;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendshipServiceUtilMethod {

    private final FriendshipRepository friendshipRepository;
    public boolean checkFriend(Members member, Members findMember) {
        boolean isFriend = false;
        if (friendshipRepository.findBySenderAndReceiverAndStatus(findMember, member, FriendshipStatus.ACCEPT).isPresent())
            isFriend = true;
        else if (friendshipRepository.findBySenderAndReceiverAndStatus(member, findMember, FriendshipStatus.ACCEPT).isPresent())
            isFriend = true;
        else if (member.getId().equals(findMember.getId())) isFriend = true;
        return isFriend;
    }

    public boolean checkFriendStatus(Members member, Members findMember) {
        boolean isPending = false;
        if (friendshipRepository.findBySenderAndReceiverAndStatus(findMember, member, FriendshipStatus.PENDING).isPresent())
            isPending = true;
        else if (friendshipRepository.findBySenderAndReceiverAndStatus(member, findMember, FriendshipStatus.PENDING).isPresent())
            isPending = true;
        else if (member.getId().equals(findMember.getId())) isPending = true;
        return isPending;
    }
}
