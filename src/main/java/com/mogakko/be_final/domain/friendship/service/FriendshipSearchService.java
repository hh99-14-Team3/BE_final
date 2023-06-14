package com.mogakko.be_final.domain.friendship.service;

import com.mogakko.be_final.domain.friendship.dto.FriendResponseDto;
import com.mogakko.be_final.domain.friendship.dto.MemberSearchRequestDto;
import com.mogakko.be_final.domain.friendship.entity.Friendship;
import com.mogakko.be_final.domain.friendship.entity.FriendshipStatus;
import com.mogakko.be_final.domain.friendship.repository.FriendshipRepository;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.exception.ErrorCode;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendshipSearchService {
    private final FriendshipRepository friendshipRepository;
    private final MembersRepository membersRepository;

    // 친구 목록 조회
    @Transactional(readOnly = true)
    public ResponseEntity<Message> getMyFriend(Members member) {
        List<Long> friendsId = new ArrayList<>();

        List<Friendship> receiverList = friendshipRepository.findAllByReceiverAndStatus(member, FriendshipStatus.ACCEPT);
        List<Friendship> senderList = friendshipRepository.findAllBySenderAndStatus(member, FriendshipStatus.ACCEPT);
        List<Friendship> findList = new ArrayList<>();
        findList.addAll(receiverList);
        findList.addAll(senderList);

//        List<Friendship> findList = friendshipRepository.findAllBySenderOrReceiverAndStatus(member, member, FriendshipStatus.ACCEPT);
        if (findList.isEmpty()) {
            return new ResponseEntity<>(new Message("조회된 친구가 없습니다.", null), HttpStatus.NOT_FOUND);
        }
        for (Friendship friendship : findList) {
            Long receiverId = friendship.getReceiver().getId();
            Long senderId = friendship.getSender().getId();

            if (receiverId.equals(member.getId())) {
                friendsId.add(senderId);
            } else {
                friendsId.add(receiverId);
            }
        }
        List<FriendResponseDto> friendsList = new ArrayList<>();

        for (Long friend : friendsId) {
            Members myFriend = membersRepository.findById(friend).orElseThrow();
            FriendResponseDto responseDto = new FriendResponseDto(myFriend, false);
            friendsList.add(responseDto);
        }
        return new ResponseEntity<>(new Message("친구 목록 조회 성공", friendsList), HttpStatus.OK);
    }

    // 받은 요청 조회
    @Transactional(readOnly = true)
    public ResponseEntity<Message> getMyFriendRequest(Members member) {
        List<Friendship> friendRequests = friendshipRepository.findAllByReceiverAndStatus(member, FriendshipStatus.PENDING);
        List<Members> friendRequestSenderList = new ArrayList<>();

        if (friendRequests.isEmpty()) {
            return new ResponseEntity<>(new Message("수신된 친구 요청이 없습니다", null), HttpStatus.OK);
        } else {
            for (Friendship friendRequest : friendRequests) {
                Members requestSender = friendRequest.getSender();
                friendRequestSenderList.add(requestSender);
            }
            return new ResponseEntity<>(new Message("친구 요청 목록 조회 성공", friendRequestSenderList), HttpStatus.OK);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> searchUser(MemberSearchRequestDto memberSearchRequestDto, UserDetailsImpl userDetails) {
        Members member = membersRepository.findById(userDetails.getMember().getId()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        String searchRequestNickname = memberSearchRequestDto.getSearchRequestNickname();
        Integer friendCode = memberSearchRequestDto.getFriendCode();

        if (!searchRequestNickname.isEmpty()) {
            return findByNicknameContain(searchRequestNickname, member);
        } else if (friendCode != null) {
            return findByFriendCode(friendCode, member);
        } else {
            return new ResponseEntity<>(new Message("nickname 혹은 friendCode 를 입력해 주세요.", null), HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<Message> findByNicknameContain(String nickname, Members member) {
        List<Members> searchedMemberList = membersRepository.findByNicknameContains(nickname);

        searchedMemberList.removeIf(searchedMember -> searchedMember.equals(member));
        searchedMemberList.removeIf(searchedMember -> checkAlreadyFriend(searchedMember, member));

        return new ResponseEntity<>(new Message("nickname 을 포함한 member List 조회 성공", searchedMemberList), HttpStatus.OK);
    }

    private ResponseEntity<Message> findByFriendCode(Integer friendCode, Members member) {
        Members findMember = membersRepository.findByFriendCode(friendCode).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        if (findMember.equals(member)) {
            return new ResponseEntity<>(new Message("자신의 친구 코드를 사용하여 자신을 검색할 수 없습니다.", null), HttpStatus.BAD_REQUEST);
        }
        if (checkAlreadyFriend(findMember, member)) {
            return new ResponseEntity<>(new Message("이미 친구임", null), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message("friendCode 를 통한 member 조회 성공", findMember), HttpStatus.OK);

    }

    private boolean checkAlreadyFriend(Members member, Members searchedMember) {
        Optional<Friendship> findRequest = friendshipRepository.findBySenderAndReceiver(member, searchedMember);
        Optional<Friendship> findReverseRequest = friendshipRepository.findBySenderAndReceiver(searchedMember, member);

        return findRequest.isPresent() || findReverseRequest.isPresent();
    }
}
