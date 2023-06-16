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

import static com.mogakko.be_final.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final MembersRepository membersRepository;
    private final FriendshipRepository friendshipRepository;
    private final NotificationSendService notificationSendService;

    // 친구 요청 (닉네임)
    public ResponseEntity<Message> friendRequest(String receiverNickname, Members member) {
        Members receiver = findMemberByNickname(receiverNickname);
        return friendRequestMethod(member, receiver);
    }

    // 친구 요청 (친구 코드)
    public ResponseEntity<Message> friendRequestByCode(Integer code, Members member) {
        Members receiver = findMemberByFriendCode(code);
        return friendRequestMethod(member, receiver);
    }

    // 친구 요청 결정
    public ResponseEntity<Message> determineRequest(DetermineRequestDto determineRequestDto, Members member) {
        Members requestSender = findMemberByNickname(determineRequestDto.getRequestSenderNickname());
        Friendship findFriendRequest = friendshipRepository.findBySenderAndReceiverAndStatus(requestSender, member, FriendshipStatus.PENDING).orElseThrow(
                () -> new CustomException(NOT_FOUND)
        );

        if (determineRequestDto.isDetermineRequest()) {
            findFriendRequest.accept();
            notificationSendService.sendAcceptNotification(member, requestSender);
            friendshipRepository.save(findFriendRequest);
            return new ResponseEntity<>(new Message("친구요청을 수락하였습니다.", null), HttpStatus.OK);
        } else {
            findFriendRequest.refuse();
            notificationSendService.sendRefuseNotification(member, requestSender);
            friendshipRepository.save(findFriendRequest);
            return new ResponseEntity<>(new Message("친구요청을 거절하였습니다.", null), HttpStatus.OK);
        }
    }

    // 친구 삭제
    public ResponseEntity<Message> deleteFriend(DeleteFriendRequestDto deleteFriendRequestDto, Members member) {
        List<String> receiverNicknameList = deleteFriendRequestDto.getReceiverNickname();
        List<Members> deleteMemberList = new ArrayList<>();
        for (String receiverNickname : receiverNicknameList) {
            Members deleteMember = findMemberByNickname(receiverNickname);
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
                throw new CustomException(USER_NOT_FOUND);
            }
        }
        return new ResponseEntity<>(new Message("친구 삭제 완료", null), HttpStatus.OK);
    }


    /**
     * Method
     */

    private ResponseEntity<Message> friendRequestMethod(Members member, Members receiver) {
        if (member.getNickname().equals(receiver.getNickname())) throw new CustomException(CANNOT_REQUEST);

        Optional<Friendship> findRequest = friendshipRepository.findBySenderAndReceiver(member, receiver)
                .or(() -> friendshipRepository.findBySenderAndReceiver(receiver, member));

        if (findRequest.isPresent()) {
            Friendship friendship = new Friendship(member, receiver, FriendshipStatus.PENDING);
            friendshipRepository.save(friendship);
            notificationSendService.sendFriendRequestNotification(member, receiver);
            return new ResponseEntity<>(new Message("친구 요청 완료", null), HttpStatus.OK);
        }

        FriendshipStatus status = findRequest.get().getStatus();
        switch (status) {
            case REFUSE:
                return new ResponseEntity<>(new Message("상대방이 요청을 거절했습니다.", null), HttpStatus.OK);
            case PENDING:
                return new ResponseEntity<>(new Message("이미 요청을 하셨습니다.", null), HttpStatus.OK);
            default:
                return new ResponseEntity<>(new Message("이미 친구로 등록된 사용자입니다.", null), HttpStatus.OK);
        }
    }

    private Members findMemberByNickname(String memberNickname) {
        return membersRepository.findByNickname(memberNickname).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
    }

    private Members findMemberByFriendCode(Integer friendCode) {
        return membersRepository.findByFriendCode(friendCode).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
    }
}
