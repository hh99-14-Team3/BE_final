package com.mogakko.be_final.domain.friendship.service;

import com.mogakko.be_final.domain.friendship.entity.Friendship;
import com.mogakko.be_final.domain.friendship.entity.FriendshipStatus;
import com.mogakko.be_final.domain.friendship.repository.FriendshipRepository;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.exception.ErrorCode;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipSearchService {
    private final FriendshipRepository friendshipRepository;
    private final MembersRepository membersRepository;

    public ResponseEntity<Message> getMyFriend(Long memberId){
        Members member = membersRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        List<Long> friendsID = new ArrayList<>();

        List<Friendship> findList = friendshipRepository.findAllBySenderOrReceiverAndStatus(member, member, FriendshipStatus.ACCEPT);
        if (findList==null){
            return new ResponseEntity<>(new Message("친구가 없습니다.", null), HttpStatus.OK);
        }
        for (Friendship friendship : findList) {

            Long receiverId = friendship.getReceiver().getId();
            Long senderId = friendship.getSender().getId();

            if (receiverId.equals(memberId)){
                friendsID.add(senderId);
            }else {
                friendsID.add(receiverId);
            }

        }
        List<Members> friendsList = new ArrayList<>();

        for (Long friend : friendsID) {
            Members myFriend = membersRepository.findById(friend).orElseThrow();
            friendsList.add(myFriend);
        }

        return new ResponseEntity<>(new Message("친구 목록 조회 성공", friendsList), HttpStatus.OK);

    }

}
