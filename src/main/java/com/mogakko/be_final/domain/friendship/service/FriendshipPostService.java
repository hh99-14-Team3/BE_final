package com.mogakko.be_final.domain.friendship.service;

import com.mogakko.be_final.domain.friendship.dto.request.DeleteFriendRequestDto;
import com.mogakko.be_final.domain.friendship.dto.request.DetermineRequestDto;
import com.mogakko.be_final.domain.friendship.dto.request.FriendRequestByCodeDto;
import com.mogakko.be_final.domain.friendship.dto.request.FriendRequestDto;
import com.mogakko.be_final.domain.friendship.entity.Friendship;
import com.mogakko.be_final.domain.friendship.entity.FriendshipStatus;
import com.mogakko.be_final.domain.friendship.entity.RejectedFriendship;
import com.mogakko.be_final.domain.friendship.repository.FriendshipRepository;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.util.MembersServiceUtilMethod;
import com.mogakko.be_final.domain.sse.service.NotificationSendService;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.redis.util.RedisUtil;
import com.mogakko.be_final.util.Message;
import com.mogakko.be_final.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.mogakko.be_final.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class FriendshipPostService {
    private final FriendshipRepository friendshipRepository;
    private final NotificationSendService notificationSendService;
    private final RedisUtil redisUtil;
    private final MembersServiceUtilMethod membersServiceUtilMethod;

    // 친구 요청 (닉네임)
    public ResponseEntity<Message> friendRequest(FriendRequestDto requestDto, Members member) {
        Members receiver = membersServiceUtilMethod.findMemberByNickname(requestDto.getRequestReceiverNickname());
        return friendRequestMethod(member, receiver);
    }

    // 친구 요청 (친구 코드)
    public ResponseEntity<Message> friendRequestByCode(FriendRequestByCodeDto requestDto, Members member) {
        Members receiver = membersServiceUtilMethod.findMemberByFriendCode(requestDto.getRequestReceiverFriendCode());
        return friendRequestMethod(member, receiver);
    }

    // 친구 요청 결정
    public ResponseEntity<Message> determineRequest(DetermineRequestDto determineRequestDto, Members member) {
        Members requestSender = membersServiceUtilMethod.findMemberByNickname(determineRequestDto.getRequestSenderNickname());
        Friendship findFriendRequest = friendshipRepository.findBySenderAndReceiverAndStatus(requestSender, member, FriendshipStatus.PENDING).orElseThrow(
                () -> new CustomException(NOT_FOUND)
        );

        if (determineRequestDto.isDetermineRequest()) {
            findFriendRequest.accept();
            notificationSendService.sendAcceptNotification(member, requestSender).subscribe();
            friendshipRepository.save(findFriendRequest);
            return new ResponseEntity<>(new Message("친구요청을 수락하였습니다.", null), HttpStatus.OK);
        } else {
            String requestSenderNickname = requestSender.getNickname();
            String memberNickname = member.getNickname();

            findFriendRequest.refuse();
            friendshipRepository.save(findFriendRequest);

            RejectedFriendship rejectedFriendship = new RejectedFriendship(requestSenderNickname, memberNickname);
            RejectedFriendship reverseRejectedFriendship = new RejectedFriendship(memberNickname, requestSenderNickname);

            String rejectedKey = "rejectedfriendship:" + requestSenderNickname + "-" + memberNickname;
            String reverseRejectedKey = "rejectedfriendship:" + memberNickname + "-" + requestSenderNickname;

            notificationSendService.sendRefuseNotification(member, requestSender).subscribe();

            redisUtil.setRejectedFriendshipWithExpireTime(rejectedKey, rejectedFriendship, 1, TimeUnit.DAYS);
            redisUtil.setRejectedFriendshipWithExpireTime(reverseRejectedKey, reverseRejectedFriendship, 1, TimeUnit.DAYS);

            return new ResponseEntity<>(new Message("친구요청을 거절하였습니다.", null), HttpStatus.OK);
        }
    }

    // 친구 삭제
    public ResponseEntity<Message> deleteFriend(DeleteFriendRequestDto deleteFriendRequestDto, Members member) {
        List<String> receiverNicknameList = deleteFriendRequestDto.getReceiverNickname();
        List<Members> deleteMemberList = new ArrayList<>();
        for (String receiverNickname : receiverNicknameList) {
            Members deleteMember = membersServiceUtilMethod.findMemberByNickname(receiverNickname);
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

    private ResponseEntity<Message> friendRequestMethod(Members member, Members receiver) {
        String senderNickname = member.getNickname();
        String receiverNickname = receiver.getNickname();
        String rejectedKey = "rejectedfriendship:" + senderNickname + "-" + receiverNickname;

        if (senderNickname.equals(receiverNickname)) throw new CustomException(CANNOT_REQUEST);

        Optional<Friendship> findRequest = friendshipRepository.findBySenderAndReceiver(member, receiver)
                .or(() -> friendshipRepository.findBySenderAndReceiver(receiver, member));


        if (redisUtil.hasKeyFriendship(rejectedKey)) {
            Long remainingTime = redisUtil.getExpire(rejectedKey, TimeUnit.SECONDS);
            String formattedTime = TimeUtil.formatSeconds(remainingTime);

            return new ResponseEntity<>(new Message("친구 요청이 거절된 상태입니다.", "남은 시간: " + formattedTime), HttpStatus.OK);
        } else if (findRequest.isPresent()) {
            FriendshipStatus status = findRequest.get().getStatus();

            if (status.equals(FriendshipStatus.REFUSE)) {
                friendshipRepository.delete(findRequest.get());

                Friendship newFriendship = new Friendship(member, receiver, FriendshipStatus.PENDING);
                friendshipRepository.save(newFriendship);
                notificationSendService.sendFriendRequestNotification(member, receiver).subscribe();

                return new ResponseEntity<>(new Message("친구 요청 완료", null), HttpStatus.OK);
            } else if (status.equals(FriendshipStatus.PENDING)) {
                return new ResponseEntity<>(new Message("이미 친구 요청을 하셨습니다.", null), HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(new Message("이미 친구로 등록된 사용자입니다.", null), HttpStatus.OK);
            }
        } else {
            Friendship newFriendship = new Friendship(member, receiver, FriendshipStatus.PENDING);
            friendshipRepository.save(newFriendship);
            notificationSendService.sendFriendRequestNotification(member, receiver).subscribe();
            return new ResponseEntity<>(new Message("친구 요청 완료", null), HttpStatus.OK);
        }
    }
}
