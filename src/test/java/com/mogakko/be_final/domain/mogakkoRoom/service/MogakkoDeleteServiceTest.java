package com.mogakko.be_final.domain.mogakkoRoom.service;

import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.entity.SocialType;
import com.mogakko.be_final.domain.mogakkoRoom.entity.LanguageEnum;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoom;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomMembers;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomMembersRepository;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomRepository;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static com.mogakko.be_final.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MogakkoDeleteServiceTest {

    @Mock
    MogakkoRoomRepository mogakkoRoomRepository;
    @Mock
    MogakkoRoomMembersRepository mogakkoRoomMembersRepository;
    @InjectMocks
    MogakkoDeleteService mogakkoDeleteService;

    Members member = Members.builder()
            .id(1L)
            .email("test@example.com")
            .nickname("nickname")
            .memberStatusCode(MemberStatusCode.BASIC)
            .mogakkoTotalTime(0L)
            .githubId("github")
            .profileImage("image")
            .role(Role.USER)
            .socialUid("id")
            .socialType(SocialType.GOOGLE)
            .password("1q2w3e4r")
            .build();

    MogakkoRoom mogakkoRoom = MogakkoRoom.builder()
            .sessionId("sessionId")
            .title("코딩킹")
            .language(LanguageEnum.JAVA)
            .lat(34.33333)
            .lon(125.333344)
            .maxMembers(4L)
            .neighborhood("가남읍")
            .isOpened(false)
            .password("3232")
            .cntMembers(2L)
            .masterMemberId(3L)
            .build();

    @Nested
    @DisplayName("모각코 방 퇴장 테스트")
    class OutMogakko {
        @DisplayName("모각코 방 퇴장 성공 테스트")
        @Test
        void outMogakko_success() {
            // given
            String sessionId = "sessionId";

            MogakkoRoomMembers mogakkoRoomMembers = MogakkoRoomMembers.builder()
                    .isEntered(true)
                    .build();

            when(mogakkoRoomRepository.findBySessionId(sessionId)).thenReturn(Optional.of(mogakkoRoom));
            when(mogakkoRoomMembersRepository.findByMemberIdAndMogakkoRoomAndIsEntered(member.getId(), mogakkoRoom, true)).thenReturn(Optional.of(mogakkoRoomMembers));

            // when
            ResponseEntity<Message> response = mogakkoDeleteService.outMogakko(sessionId, member);

            // then
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody().getMessage(), "모각코 퇴장 성공");
            assertEquals(mogakkoRoom.getCntMembers(), 1L);
            assertEquals(mogakkoRoomMembers.isEntered(), false);
        }

        @DisplayName("모각코 방 퇴장 & 삭제 성공 테스트")
        @Test
        void outMogakko_mogakkoRoomDelete() {
            // given
            String sessionId = "sessionId";

            MogakkoRoom mogakkoRoom = MogakkoRoom.builder()
                    .sessionId("sessionId")
                    .title("코딩킹")
                    .language(LanguageEnum.JAVA)
                    .lat(34.33333)
                    .lon(125.333344)
                    .maxMembers(4L)
                    .neighborhood("가남읍")
                    .isOpened(false)
                    .password("3232")
                    .cntMembers(1L)
                    .masterMemberId(3L)
                    .build();

            MogakkoRoomMembers mogakkoRoomMembers = MogakkoRoomMembers.builder()
                    .isEntered(true)
                    .build();

            when(mogakkoRoomRepository.findBySessionId(sessionId)).thenReturn(Optional.of(mogakkoRoom));
            when(mogakkoRoomMembersRepository.findByMemberIdAndMogakkoRoomAndIsEntered(member.getId(), mogakkoRoom, true)).thenReturn(Optional.of(mogakkoRoomMembers));

            // when
            ResponseEntity<Message> response = mogakkoDeleteService.outMogakko(sessionId, member);

            // then
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody().getMessage(), "모각코 퇴장 및 방 삭제 성공");
            assertEquals(mogakkoRoomMembers.isEntered(), false);
        }

        @DisplayName("존재하지 않는 모각코 방 퇴장 테스트")
        @Test
        void outMogakko_noMogakkoRoom() {
            // given
            String sessionId = "noSessionId";

            when(mogakkoRoomRepository.findBySessionId(sessionId)).thenReturn(Optional.empty());

            // when & then
            CustomException customException = assertThrows(CustomException.class, () -> mogakkoDeleteService.outMogakko(sessionId, member));
            assertEquals(customException.getErrorCode(), MOGAKKO_NOT_FOUND);
        }

        @DisplayName("모각코 방에 존재하지 않는 멤버 퇴장 테스트")
        @Test
        void outMogakko_noMogakkoRoomMember() {
            // given
            String sessionId = "SessionId";

            when(mogakkoRoomRepository.findBySessionId(sessionId)).thenReturn(Optional.of(mogakkoRoom));
            when(mogakkoRoomMembersRepository.findByMemberIdAndMogakkoRoomAndIsEntered(member.getId(), mogakkoRoom, true)).thenReturn(Optional.empty());

            // when & then
            CustomException customException = assertThrows(CustomException.class, () -> mogakkoDeleteService.outMogakko(sessionId, member));
            assertEquals(customException.getErrorCode(), NOT_MOGAKKO_MEMBER);
        }

        @DisplayName("이미 나간 유저 모각코 방 퇴장 테스트")
        @Test
        void outMogakko_alreadyOutMember() {
            // given
            String sessionId = "SessionId";

            MogakkoRoomMembers mogakkoRoomMembers = MogakkoRoomMembers.builder()
                    .isEntered(false)
                    .build();

            when(mogakkoRoomRepository.findBySessionId(sessionId)).thenReturn(Optional.of(mogakkoRoom));
            when(mogakkoRoomMembersRepository.findByMemberIdAndMogakkoRoomAndIsEntered(member.getId(), mogakkoRoom, true)).thenReturn(Optional.of(mogakkoRoomMembers));

            // when & then
            CustomException customException = assertThrows(CustomException.class, () -> mogakkoDeleteService.outMogakko(sessionId, member));
            assertEquals(customException.getErrorCode(), ALREADY_OUT_MEMBER);
        }
    }
}