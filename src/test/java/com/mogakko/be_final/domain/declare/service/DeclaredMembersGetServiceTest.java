package com.mogakko.be_final.domain.declare.service;

import com.mogakko.be_final.domain.declare.entity.DeclaredMembers;
import com.mogakko.be_final.domain.declare.entity.DeclaredReason;
import com.mogakko.be_final.domain.declare.repository.DeclaredMembersRepository;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.Message;
import lombok.Builder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;

import static com.mogakko.be_final.exception.ErrorCode.INVALID_FRIEND_CODE;
import static com.mogakko.be_final.exception.ErrorCode.NOT_ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@DisplayName("Declared Members Service - [GET] 테스트")
@ExtendWith({MockitoExtension.class})
class DeclaredMembersGetServiceTest {
    @Mock
    DeclaredMembersRepository declaredMembersRepository;
    @InjectMocks
    DeclaredMembersGetService declaredMembersGetService;

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

    Members notAdminMember = Members.builder()
            .id(1L)
            .email("test@example.com")
            .nickname("nickname")
            .password("password1!")
            .role(Role.USER)
            .codingTem(36.5)
            .mogakkoTotalTime(0L)
            .memberStatusCode(MemberStatusCode.BASIC)
            .profileImage("https://source.boringavatars.com/beam/120/$" + "nickname" + "?colors=00F0FF,172435,394254,EAEBED,F9F9FA")
            .friendCode(123456)
            .isTutorialCheck(false)
            .build();

    @DisplayName("신고된 유저 확인 성공 테스트")
    @Test
    void getReportedMembers() {
        List<DeclaredMembers> declaredMembersList = new ArrayList<>();
        when(declaredMembersRepository.findAll()).thenReturn(declaredMembersList);

        ResponseEntity<Message> response = declaredMembersGetService.getReportedMembers(member);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getMessage(), "신고된 멤버 조회 성공");
    }

}