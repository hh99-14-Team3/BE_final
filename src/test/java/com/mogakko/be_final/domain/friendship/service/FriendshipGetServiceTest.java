package com.mogakko.be_final.domain.friendship.service;

import com.mogakko.be_final.domain.friendship.entity.Friendship;
import com.mogakko.be_final.domain.friendship.entity.FriendshipStatus;
import com.mogakko.be_final.domain.friendship.repository.FriendshipRepository;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.util.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Friendship Controller - [GET] 테스트")
class FriendshipGetServiceTest {
    @Mock
    MembersRepository membersRepository;
    @Mock
    FriendshipRepository friendshipRepository;
    @InjectMocks
    FriendshipGetService friendshipGetService;

    Members member = Members.builder()
            .id(1L)
            .email("test@example.com")
            .nickname("nickname")
            .password("password1!")
            .role(Role.ADMIN)
            .codingTem(36.5)
            .mogakkoTotalTime(0L)
            .memberStatusCode(MemberStatusCode.BASIC)
            .profileImage("https://source.boringavatars.com/beam/120/$" + "nickname" + "?colors=00F0FF,172435,394254,EAEBED,F9F9FA")
            .friendCode(123456)
            .isTutorialCheck(false)
            .build();

    @DisplayName("[GET] 친구 목록 조회 성공 테스트 - 조회된 친구 없음")
    @Test
    void getMyFriend() {
        List<Friendship> friendshipList = new ArrayList<>();
        when(friendshipRepository.findAllByReceiverAndStatusOrSenderAndStatus(member, FriendshipStatus.ACCEPT, member, FriendshipStatus.ACCEPT)).thenReturn(friendshipList);

        ResponseEntity<Message> response = friendshipGetService.getMyFriend(member);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getMessage(), "조회된 친구가 없습니다.");
    }

}