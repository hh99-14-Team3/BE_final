package com.mogakko.be_final.domain.friendship.service;

import com.mogakko.be_final.domain.friendship.dto.DeleteFriendRequestDto;
import com.mogakko.be_final.domain.friendship.dto.DetermineRequestDto;
import com.mogakko.be_final.domain.friendship.entity.Friendship;
import com.mogakko.be_final.domain.friendship.entity.FriendshipStatus;
import com.mogakko.be_final.domain.friendship.repository.FriendshipRepository;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.domain.sse.service.NotificationSendService;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mogakko.be_final.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final MembersRepository membersRepository;
    private final FriendshipRepository friendshipRepository;
    private final NotificationSendService notificationSendService;

    // 친구 요청
    public ResponseEntity<Message> friendRequest(String receiverNickname, Members member) {

        Members receiver = membersRepository.findByNickname(receiverNickname).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );

        if (member.getNickname().equals(receiverNickname)) {
            return new ResponseEntity<>(new Message("자신에게 친구 요청을 할 수 없습니다.", null), HttpStatus.BAD_REQUEST);
        }

        Optional<Friendship> findRequest = friendshipRepository.findBySenderAndReceiver(member, receiver);
        Optional<Friendship> findReverseRequest = friendshipRepository.findBySenderAndReceiver(receiver, member);

        if (findRequest.isEmpty() && findReverseRequest.isEmpty()) {
            Friendship friendship = new Friendship(member, receiver, FriendshipStatus.PENDING);
            friendshipRepository.save(friendship);
            notificationSendService.sendFriendRequestNotification(member, receiver);
            return new ResponseEntity<>(new Message("친구 요청 완료", null), HttpStatus.OK);
        }

        if (findRequest.isPresent()) {
            Friendship request = findRequest.get();
            if (request.getStatus() == FriendshipStatus.REFUSE) {
                return new ResponseEntity<>(new Message("상대방이 요청을 거절했습니다.", null), HttpStatus.OK);
            } else if (request.getStatus() == FriendshipStatus.PENDING) {
                return new ResponseEntity<>(new Message("이미 요청을 하셨습니다.", null), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new Message("이미 친구로 등록된 사용자입니다.", null), HttpStatus.OK);
    }

    // 친구 요청 결정
    public ResponseEntity<Message> determineRequest(DetermineRequestDto determineRequestDto, Members member) {
        Members requestSender = findMember(determineRequestDto.getRequestSenderNickname());
        Friendship findFriendRequest = findPendingFriendship(requestSender, member);

        if (determineRequestDto.isDetermineRequest()) {
            findFriendRequest.accept();
            notificationSendService.sendAcceptNotification(member, requestSender);
            friendshipRepository.save(findFriendRequest);
            return new ResponseEntity<>(new Message("친구요청이 수락되었습니다.", null), HttpStatus.OK);
        } else {
            findFriendRequest.refuse();
            notificationSendService.sendRefuseNotification(member, requestSender);
            friendshipRepository.save(findFriendRequest);
            return new ResponseEntity<>(new Message("친구요청이 거절되었습니다.", null), HttpStatus.OK);
        }
    }

    // 친구 삭제
    public ResponseEntity<Message> deleteFriend(DeleteFriendRequestDto deleteFriendRequestDto, Members member) {
        List<String> receiverNicknameList = deleteFriendRequestDto.getReceiverNickname();
        List<Members> deleteMemberList = new ArrayList<>();
        for (String receiverNickname : receiverNicknameList) {
            Members deleteMember = membersRepository.findByNickname(receiverNickname).orElseThrow(
                    () -> new CustomException(USER_NOT_FOUND)
            );
            deleteMemberList.add(deleteMember);
        }

        for (Members requestReceiver : deleteMemberList) {
            Optional<Friendship> friendship = friendshipRepository.findBySenderAndReceiver(member, requestReceiver).or(
                    () -> friendshipRepository.findBySenderAndReceiver(requestReceiver, member)
            );
            if (friendship.isPresent()) {
                Friendship findFriendship = friendship.get();
                friendshipRepository.delete(findFriendship);
            } else {
                return new ResponseEntity<>(new Message("삭제 대상이 존재하지 않습니다.", null), HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(new Message("친구 삭제 완료", null), HttpStatus.OK);
    }

    public ResponseEntity<Message> friendRequestByCode(Integer code, Members requestSender) {
        Members requestReceiver = membersRepository.findByFriendCode(code).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
        return friendRequest(requestReceiver.getNickname(), requestSender);
    }


    /**
     * Method
     */

    private Members findMember(String memberNickname) {
        return membersRepository.findByNickname(memberNickname).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
    }

    private Friendship findPendingFriendship(Members sender, Members receiver) {
        return friendshipRepository.findBySenderAndReceiverAndStatus(sender, receiver, FriendshipStatus.PENDING).get();
    }
}
