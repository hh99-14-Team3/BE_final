package com.mogakko.be_final.domain.friendship.service;

import com.mogakko.be_final.domain.friendship.dto.DeleteFriendRequestDto;
import com.mogakko.be_final.domain.friendship.dto.DetermineRequestDto;
import com.mogakko.be_final.domain.friendship.dto.FriendRequestDto;
import com.mogakko.be_final.domain.friendship.entity.Friendship;
import com.mogakko.be_final.domain.friendship.entity.FriendshipStatus;
import com.mogakko.be_final.domain.friendship.repository.FriendshipRepository;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.domain.sse.entity.Notification;
import com.mogakko.be_final.domain.sse.repository.NotificationRepository;
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
    private final NotificationRepository notificationRepository;

    public ResponseEntity<Message> friendRequest(FriendRequestDto friendRequestDto) {
        String senderEmail = friendRequestDto.getSender();
        String receiverEmail = friendRequestDto.getReceiver();

        Members sender = membersRepository.findByEmail(senderEmail).orElseThrow(
                () -> new CustomException(ErrorCode.EMAIL_NOT_FOUND)
        );

        Members receiver = membersRepository.findByEmail(receiverEmail).orElseThrow(
                () -> new CustomException(ErrorCode.EMAIL_NOT_FOUND)
        );

        Optional<Friendship> findRequest = friendshipRepository.findAllBySenderAndReceiver(sender, receiver);
        Optional<Friendship> findReverseRequest = friendshipRepository.findAllBySenderAndReceiver(receiver, sender);

        if (findRequest.isEmpty() && findReverseRequest.isEmpty()) {
            Friendship friendship = new Friendship(sender, receiver, FriendshipStatus.PENDING);
            friendshipRepository.save(friendship);

            notificationSendService.sendFriendRequestNotification(sender, receiver);

            return new ResponseEntity<>(new Message("친구 요청 완료", null), HttpStatus.OK);

        } else {
            if(findRequest.isPresent()){
                Friendship request = findRequest.get();
                if (request.getStatus() == FriendshipStatus.REFUSE) {
                    return new ResponseEntity<>(new Message("상대방이 친구요청을 거절했습니다.", null), HttpStatus.OK);
                } else if (request.getStatus() == FriendshipStatus.PENDING) {
                    return new ResponseEntity<>(new Message("이미 친구요청을 하셨습니다.", null), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(new Message("이미 친구로 등록된 사용자입니다.", null), HttpStatus.OK);
                }
            } else {
                Friendship reverseRequest = findReverseRequest.get();
                if (reverseRequest.getStatus() == FriendshipStatus.REFUSE) {
                    return new ResponseEntity<>(new Message("당신이 친구요청을 거절했습니다.", null), HttpStatus.OK);
                } else if (reverseRequest.getStatus() == FriendshipStatus.PENDING) {
                    return new ResponseEntity<>(new Message("당신에게 이미 친구요청이 왔습니다.", null), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(new Message("이미 친구로 등록된 사용자입니다.", null), HttpStatus.OK);
                }

            }

        }
    }

    // 친구요청 결정 하는 ( 수락할지 , 거절할지 )
    public ResponseEntity<Message> determineRequest(DetermineRequestDto determineRequestDto){
        Notification findNotification = findNotification(determineRequestDto.getNotificationId());

        Members requestSender = findMember(determineRequestDto.getSenderId());
        Members requestReceiver = findMember(determineRequestDto.getReceiverId());

        Friendship findFriendRequest = findPendingFriendship(requestSender, requestReceiver);

        if(determineRequestDto.isDetermineRequest()){
            findFriendRequest.accept();
            // requestReceiver 가 알림을 받아야 하므로 자리가 바뀌어야함 다시말해 requestReceiver 가 SSE sender 가 되어야한다는 의미
            notificationSendService.sendAcceptNotification(requestReceiver, requestSender);
            friendshipRepository.save(findFriendRequest);
            return new ResponseEntity<>(new Message("친구요청이 수락 되었습니다.",null), HttpStatus.OK);
        }else {
            findFriendRequest.refuse();
            notificationSendService.sendRefuseNotification(requestReceiver, requestSender);
            friendshipRepository.save(findFriendRequest);
            return new ResponseEntity<>(new Message("친구요청이 거절 되었습니다.",null), HttpStatus.OK);
        }

    }

    public ResponseEntity<Message> deleteFriend(DeleteFriendRequestDto deleteFriendRequestDto){
        Members requestSender = membersRepository.findByEmail(deleteFriendRequestDto.getSenderEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        Members requestReceiver = membersRepository.findByEmail(deleteFriendRequestDto.getReceiverEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        if(friendshipRepository.findAllBySenderOrReceiver(requestSender, requestReceiver).isPresent()){
            Friendship findFriendship = friendshipRepository.findAllBySenderOrReceiver(requestSender, requestReceiver).get();
            friendshipRepository.delete(findFriendship);
        }else if(friendshipRepository.findAllBySenderOrReceiver(requestReceiver, requestSender).isPresent()){
            Friendship findFriendship = friendshipRepository.findAllBySenderOrReceiver(requestReceiver, requestSender).get();
            friendshipRepository.delete(findFriendship);
        }else {
            throw new CustomException(ErrorCode.FRIEND_NOT_FOUND);
        }

        return new ResponseEntity<>(new Message("친구 삭제가 완료 되었습니다.", null), HttpStatus.OK);
    }

    private Members findMember(Long memberId) {
        return membersRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
    }

    private Notification findNotification(Long notificationId) {
        return notificationRepository.findById(notificationId).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_NOTIFICATION_ID)
        );
    }

    private Friendship findPendingFriendship(Members sender, Members receiver) {
        return friendshipRepository.findBySenderAndReceiverAndStatus(
                sender, receiver, FriendshipStatus.PENDING);
    }

}
