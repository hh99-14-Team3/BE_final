package com.mogakko.be_final.domain.directMessage.controller;

import com.mogakko.be_final.domain.directMessage.dto.request.DirectMessageDeleteRequestDto;
import com.mogakko.be_final.domain.directMessage.dto.request.DirectMessageSendRequestDto;
import com.mogakko.be_final.domain.directMessage.service.DirectMessagePostService;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Direct Controller - [POST] 테스트")
class DirectMessagePostControllerTest {
    @Mock
    DirectMessagePostService directMessagePostService;
    @Mock
    DirectMessageSendRequestDto directMessageSendRequestDto;
    @Mock
    DirectMessageDeleteRequestDto directMessageDeleteRequestDto;
    @InjectMocks
    DirectMessagePostController directMessagePostController;

    Members member = Members.builder()
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
    UserDetailsImpl userDetails = new UserDetailsImpl(member, member.getEmail());

    @DisplayName("[POST] 쪽지 전송 테스트")
    @Test
    void sendDirectMessage() {
        Message message = new Message("쪽지 전송 성공", null);
        when(directMessagePostService.sendDirectMessage(any(Members.class), any(DirectMessageSendRequestDto.class))).thenReturn(ResponseEntity.ok(message));

        ResponseEntity<Message> response = directMessagePostController.sendDirectMessage(userDetails, directMessageSendRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(message, response.getBody());
    }

    @DisplayName("[POST] 쪽지 삭제 테스트")
    @Test
    void deleteDirectMessage() {
        Message message = new Message("쪽지 삭제가 완료되었습니다.", null);
        when(directMessagePostService.deleteDirectMessage(any(Members.class), anyList())).thenReturn(ResponseEntity.ok(message));

        ResponseEntity<Message> response = directMessagePostController.deleteDirectMessage(userDetails, directMessageDeleteRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(message, response.getBody());
    }
}