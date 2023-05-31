package com.mogakko.be_final.domain.friendship.service;

import com.mogakko.be_final.domain.friendship.dto.FriendRequestDto;
import com.mogakko.be_final.domain.friendship.entity.Friendship;
import com.mogakko.be_final.domain.friendship.entity.FriendshipStatus;
import com.mogakko.be_final.domain.friendship.repository.FriendshipRepository;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.domain.sse.service.NotificationSendService;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.exception.ErrorCode;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final MembersRepository membersRepository;
    private final FriendshipRepository friendshipRepository;
    private final NotificationSendService notificationSendService;

    public ResponseEntity<Message> friendRequest(FriendRequestDto friendRequestDto) {
        String senderEmail = friendRequestDto.getSender();
        String receiverEmail = friendRequestDto.getReceiver();

        Members sender = membersRepository.findByEmail(senderEmail).orElseThrow(
                () -> new CustomException(ErrorCode.EMAIL_NOT_FOUND)
        );

        Members receiver = membersRepository.findByEmail(receiverEmail).orElseThrow(
                () -> new CustomException(ErrorCode.EMAIL_NOT_FOUND)
        );

        Optional<Friendship> findRequest = friendshipRepository.findAllBySenderOrReceiver(sender, receiver);

        if (findRequest.isEmpty()) {
            Friendship friendship = new Friendship(sender, receiver, FriendshipStatus.PENDING);
            friendshipRepository.save(friendship);

            notificationSendService.sendFriendRequestNotification(sender, receiver);

            return new ResponseEntity<>(new Message("친구 요청 완료", null), HttpStatus.OK);

        } else {
            if (findRequest.get().getStatus() == FriendshipStatus.REFUSE) {
                // 친구 요청을 거절했을 시. 하루뒤에 다시 신청 가능하게 할 것
                return new ResponseEntity<>(new Message("상대방이 친구요청을 거절했습니다.", null), HttpStatus.OK);
            } else if (findRequest.get().getStatus() == FriendshipStatus.PENDING) {
                // 친구 요청이 완료 되지 않음. 즉, 보류 상태
                return new ResponseEntity<>(new Message("이미 진행중인 친구 요청이 있습니다.", null), HttpStatus.OK);
            } else {
                // 이미 친구 일시
                return new ResponseEntity<>(new Message("이미 친구로 등록된 사용자입니다.", null), HttpStatus.OK);
            }

        }

    }

    // 친구요청 결정 하는 ( 수락할지 , 거절할지 )
    public ResponseEntity<Message> determineRequest (){

        return new ResponseEntity<>(new Message("a",null), HttpStatus.OK);

    }

}
