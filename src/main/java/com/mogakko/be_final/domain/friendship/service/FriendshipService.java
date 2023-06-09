package com.mogakko.be_final.domain.friendship.service;

import com.mogakko.be_final.domain.friendship.dto.DeleteFriendRequestDto;
import com.mogakko.be_final.domain.friendship.dto.DetermineRequestDto;
import com.mogakko.be_final.domain.friendship.dto.FriendRequestDto;
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

import java.util.Optional;

import static com.mogakko.be_final.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final MembersRepository membersRepository;
    private final FriendshipRepository friendshipRepository;
    private final NotificationSendService notificationSendService;

    // 친구 요청
    public ResponseEntity<Message> friendRequest(FriendRequestDto friendRequestDto, Members member) {
        String receiverNickname = friendRequestDto.getRequestReceiverNickname();

        Members receiver = membersRepository.findByNickname(receiverNickname).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );

        if (member == receiver) {
            return new ResponseEntity<>(new Message("자신에게는 친구 요청 할 수 없음", null), HttpStatus.BAD_REQUEST);
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
                return new ResponseEntity<>(new Message("상대방이 친구 요청을 거절했습니다.", null), HttpStatus.OK);
            } else if (request.getStatus() == FriendshipStatus.PENDING) {
                return new ResponseEntity<>(new Message("이미 친구 요청을 하셨습니다.", null), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new Message("이미 친구로 등록된 사용자입니다.", null), HttpStatus.OK);

        /**
         * 친구 요청 로직에서 이 밑부분까지 예외처리가 필요한지 궁금합니다.
         * 친구 요청을 보낼 때 이미 보내려고 하는 사람에게서 요청이 와있거나 거절했을 경우에 저렇게 리턴하면 한번 거절하면 영원히 친구를 못하게 되는건지,
         * 이미 요청이 와있을때에는 요청을 보내면 안되는 이유가 있는 건지 궁금합니다.
         */
//        else {
//            Friendship reverseRequest = findReverseRequest.get();
//            if (reverseRequest.getStatus() == FriendshipStatus.REFUSE) {
//                return new ResponseEntity<>(new Message("당신이 친구 요청을 거절했습니다.", null), HttpStatus.OK);
//            } else if (reverseRequest.getStatus() == FriendshipStatus.PENDING) {
//                return new ResponseEntity<>(new Message("당신에게 이미 친구 요청이 왔습니다.", null), HttpStatus.OK);
//            } else {
//                return new ResponseEntity<>(new Message("이미 친구로 등록된 사용자입니다.", null), HttpStatus.OK);
//            }
//        }

    }

    // 친구 요청 결정
    public ResponseEntity<Message> determineRequest(DetermineRequestDto determineRequestDto, Members member) {
        Members requestSender = findMember(determineRequestDto.getRequestSenderNickname());
        Friendship findFriendRequest = findPendingFriendship(requestSender, member);

        if (determineRequestDto.isDetermineRequest()) {
            findFriendRequest.accept();
            notificationSendService.sendAcceptNotification(member, requestSender);
            friendshipRepository.save(findFriendRequest);
            return new ResponseEntity<>(new Message("친구요청이 수락 되었습니다.", null), HttpStatus.OK);
        } else {
            findFriendRequest.refuse();
            notificationSendService.sendRefuseNotification(member, requestSender);
            friendshipRepository.save(findFriendRequest);
            return new ResponseEntity<>(new Message("친구요청이 거절 되었습니다.", null), HttpStatus.OK);
        }
    }

    // 친구 삭제
    public ResponseEntity<Message> deleteFriend(DeleteFriendRequestDto deleteFriendRequestDto, Members member) {
        Members requestReceiver = findMember(deleteFriendRequestDto.getReceiverNickname());

        /**
         * 여기 로직은 db를 두번씩 터치하는게 안좋게 느껴져서 똑같이 돌아가게 리펙토링만 해놓았습니다.
         */
        Optional<Friendship> friendship = friendshipRepository.findBySenderOrReceiver(member, requestReceiver).or(
                () -> friendshipRepository.findBySenderOrReceiver(requestReceiver, member)
        );
        if (friendship.isPresent()) {
            Friendship findFriendship = friendship.get();
            friendshipRepository.delete(findFriendship);
        } else {
            return new ResponseEntity<>(new Message("삭제 대상이 존재하지 않습니다.", null), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new Message("친구 삭제가 완료 되었습니다.", "삭제된 사용자 : " + requestReceiver.getNickname()), HttpStatus.OK);
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
