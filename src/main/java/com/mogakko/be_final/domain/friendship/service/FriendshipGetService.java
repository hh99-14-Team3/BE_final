package com.mogakko.be_final.domain.friendship.service;

import com.mogakko.be_final.domain.friendship.dto.response.FriendResponseDto;
import com.mogakko.be_final.domain.friendship.entity.Friendship;
import com.mogakko.be_final.domain.friendship.entity.FriendshipStatus;
import com.mogakko.be_final.domain.friendship.repository.FriendshipRepository;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mogakko.be_final.exception.ErrorCode.CANNOT_FOUND_FRIEND;

@Service
@RequiredArgsConstructor
public class FriendshipGetService {

    private final MembersRepository membersRepository;
    private final FriendshipRepository friendshipRepository;

    // 친구 목록 조회
    @Transactional
    public ResponseEntity<Message> getMyFriend(Members member) {
        List<Friendship> friendshipList = friendshipRepository.findAllByReceiverAndStatusOrSenderAndStatus(member, FriendshipStatus.ACCEPT, member, FriendshipStatus.ACCEPT);

        if (friendshipList.isEmpty()) {
            return new ResponseEntity<>(new Message("조회된 친구가 없습니다.", null), HttpStatus.OK);
        }

        List<FriendResponseDto> friendsList = new ArrayList<>();

        for (Friendship friendship : friendshipList) {
            Long receiverId = friendship.getReceiver().getId();
            Long senderId = friendship.getSender().getId();
            Long friendId = receiverId.equals(member.getId()) ? senderId : receiverId;

            Members myFriend = membersRepository.findById(friendId)
                    .orElseThrow(() -> new CustomException(CANNOT_FOUND_FRIEND));
            FriendResponseDto responseDto = new FriendResponseDto(myFriend, false);
            friendsList.add(responseDto);
        }

        return new ResponseEntity<>(new Message("친구 목록 조회 성공", friendsList), HttpStatus.OK);
    }

    // 받은 요청 조회
    @Transactional(readOnly = true)
    public ResponseEntity<Message> getMyFriendRequest(Members member) {
        List<Members> friendRequestSenderList = friendshipRepository.findAllByReceiverAndStatus(member, FriendshipStatus.PENDING)
                .stream().map(Friendship::getSender).collect(Collectors.toList());

        if (friendRequestSenderList.isEmpty()) {
            return new ResponseEntity<>(new Message("수신된 친구 요청이 없습니다", null), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Message("친구 요청 목록 조회 성공", friendRequestSenderList), HttpStatus.OK);
        }
    }

}
