package com.mogakko.be_final.domain.declare.service;

import com.mogakko.be_final.domain.declare.entity.DeclaredMembers;
import com.mogakko.be_final.domain.declare.entity.DeclaredReason;
import com.mogakko.be_final.domain.declare.repository.DeclaredMembersRepository;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static com.mogakko.be_final.domain.members.entity.MemberStatusCode.BAD_GATE_WAY;
import static com.mogakko.be_final.domain.members.entity.MemberStatusCode.BAD_REQUEST;
import static com.mogakko.be_final.domain.members.entity.Role.PROHIBITION;
import static com.mogakko.be_final.domain.members.entity.Role.USER;
import static com.mogakko.be_final.exception.ErrorCode.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Declare Controller - [PUT] 테스트")
class DeclaredMembersPutServiceTest {

    @Mock
    DeclaredMembersRepository declaredMembersRepository;
    @Mock
    MembersRepository membersRepository;
    @InjectMocks
    DeclaredMembersPutService declaredMembersPutService;

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

    Members declaredMember = Members.builder()
            .id(2L)
            .email("test1@example.com")
            .nickname("nickname1")
            .password("password2!")
            .role(Role.USER)
            .codingTem(36.5)
            .mogakkoTotalTime(0L)
            .memberStatusCode(MemberStatusCode.BASIC)
            .profileImage("https://source.boringavatars.com/beam/120/$" + "nickname" + "?colors=00F0FF,172435,394254,EAEBED,F9F9FA")
            .friendCode(123456)
            .isTutorialCheck(false)
            .declared(0)
            .build();

    Members declaredMember1 = Members.builder()
            .id(2L)
            .email("test1@example.com")
            .nickname("nickname2")
            .password("password3!")
            .role(Role.USER)
            .codingTem(36.5)
            .mogakkoTotalTime(0L)
            .memberStatusCode(MemberStatusCode.BAD_REQUEST)
            .profileImage("https://source.boringavatars.com/beam/120/$" + "nickname" + "?colors=00F0FF,172435,394254,EAEBED,F9F9FA")
            .friendCode(123456)
            .isTutorialCheck(false)
            .declared(1)
            .build();

    Members declaredMember2 = Members.builder()
            .id(2L)
            .email("test1@example.com")
            .nickname("nickname3")
            .password("password4!")
            .role(USER)
            .codingTem(36.5)
            .mogakkoTotalTime(0L)
            .memberStatusCode(BAD_GATE_WAY)
            .profileImage("https://source.boringavatars.com/beam/120/$" + "nickname" + "?colors=00F0FF,172435,394254,EAEBED,F9F9FA")
            .friendCode(123456)
            .isTutorialCheck(false)
            .declared(-1)
            .build();

    DeclaredMembers declaredMembers = DeclaredMembers.builder().id(declaredMember.getId()).reporterNickname("nickname").declaredMember(declaredMember).declaredReason(DeclaredReason.ABUSE).reason("").build();
    DeclaredMembers declaredMembers1 = DeclaredMembers.builder().id(declaredMember1.getId()).reporterNickname("nickname").declaredMember(declaredMember1).declaredReason(DeclaredReason.ABUSE).reason("").build();
    DeclaredMembers declaredMembers2 = DeclaredMembers.builder().id(declaredMember2.getId()).reporterNickname("nickname").declaredMember(declaredMember2).declaredReason(DeclaredReason.ABUSE).reason("").build();

    @DisplayName("[PUT] 관리자 페이지 연결 성공 테스트")
    @Test
    void handleReport() {
        // Given
        when(declaredMembersRepository.findById(declaredMembers.getId())).thenReturn(Optional.of(declaredMembers));
        when(membersRepository.save(declaredMember)).thenReturn(null);
        // When
        ResponseEntity<Message> response = declaredMembersPutService.handleReport(declaredMember.getId(), member);
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("신고 처리 완료", response.getBody().getMessage());
    }
}