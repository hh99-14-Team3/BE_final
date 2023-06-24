package com.mogakko.be_final.domain.members.service;

import com.mogakko.be_final.domain.directMessage.entity.DirectMessage;
import com.mogakko.be_final.domain.directMessage.repository.DirectMessageRepository;
import com.mogakko.be_final.domain.friendship.repository.FriendshipRepository;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MemberWeekStatisticsRepository;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomMembersLanguageStatisticsRepository;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomMembersRepository;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MembersDeleteService {

    private final DirectMessageRepository directMessageRepository;
    private final MogakkoRoomMembersRepository mogakkoRoomMembersRepository;
    private final FriendshipRepository friendshipRepository;
    private final MembersRepository membersRepository;
    private final MemberWeekStatisticsRepository memberWeekStatisticsRepository;
    private final MogakkoRoomMembersLanguageStatisticsRepository mogakkoRoomMembersLanguageStatisticsRepository;

    // 회원 탈퇴
    @Transactional
    public ResponseEntity<Message> withdrawMember(Members member) {
        memberWeekStatisticsRepository.deleteByEmail(member.getEmail());
        mogakkoRoomMembersLanguageStatisticsRepository.deleteByEmail(member.getEmail());
        friendshipRepository.deleteAllBySenderAndReceiver(member, member);
        mogakkoRoomMembersRepository.deleteById(member.getId());
        List<DirectMessage> dmList = directMessageRepository.findAllBySenderOrReceiver(member, member);
        for (DirectMessage directMessage : dmList) {
            directMessage.deleteMember(member);
        }
        membersRepository.delete(member);

        return new ResponseEntity<>(new Message("회원 탈퇴 성공", null), HttpStatus.OK);
    }
}
