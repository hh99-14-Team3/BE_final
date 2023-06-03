package com.mogakko.be_final.domain.friendship.service;

import com.mogakko.be_final.domain.friendship.entity.Friendship;
import com.mogakko.be_final.domain.friendship.entity.FriendshipStatus;
import com.mogakko.be_final.domain.friendship.repository.FriendshipRepository;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
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

    public ResponseEntity<Message> getMyFriend(UserDetailsImpl userDetails){
        Members member = userDetails.getMember();
        Long memberId = member.getId();

        List<Long> friendsID = new ArrayList<>();

        List<Friendship> findList = friendshipRepository.findAllBySenderOrReceiverAndStatus(member, member, FriendshipStatus.ACCEPT);
        if (findList==null){
            return new ResponseEntity<>(new Message("조회된 친구가 없습니다.", null), HttpStatus.NOT_FOUND);
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


    public ResponseEntity<Message> getMyFriendRequest(UserDetailsImpl userDetails){
        Members receiver = userDetails.getMember();

        List<Friendship> friendRequests = friendshipRepository.findAllByReceiverAndStatus(receiver, FriendshipStatus.PENDING);
        List<Members> friendRequestSenderList = new ArrayList<>();

        if(friendRequests.isEmpty()){
            return new ResponseEntity<>(new Message("수신된 친구 요청이 없습니다", null),HttpStatus.OK);
        }else{
            for (Friendship friendRequest : friendRequests) {
                Members requestSender = friendRequest.getSender();
                friendRequestSenderList.add(requestSender);
            }
            return new ResponseEntity<>(new Message("친구 요청 목록 조회 성공", friendRequestSenderList),HttpStatus.OK);
        }


    }

}
