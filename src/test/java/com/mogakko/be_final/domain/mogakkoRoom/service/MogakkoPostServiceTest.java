package com.mogakko.be_final.domain.mogakkoRoom.service;

import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.entity.SocialType;
import com.mogakko.be_final.domain.mogakkoRoom.dto.request.MogakkoRoomCreateRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.dto.request.MogakkoRoomEnterDataRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.dto.response.MogakkoRoomCreateResponseDto;
import com.mogakko.be_final.domain.mogakkoRoom.entity.LanguageEnum;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoom;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomMembers;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomMembersLanguageStatisticsRepository;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomMembersRepository;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomRepository;
import com.mogakko.be_final.domain.mogakkoRoom.util.MogakkoServiceUtilMethod;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.BadWordFiltering;
import com.mogakko.be_final.util.Message;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.mogakko.be_final.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MogakkoPostServiceTest {
    @Mock
    BadWordFiltering badWordFiltering;

    @Mock
    MogakkoRoomRepository mogakkoRoomRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    MogakkoRoomMembersRepository mogakkoRoomMembersRepository;
    @Mock
    MogakkoRoomMembersLanguageStatisticsRepository mogakkoRoomMembersLanguageStatisticsRepository;
    @Mock
    MogakkoServiceUtilMethod mogakkoServiceUtilMethod;

    @InjectMocks
    MogakkoPostService mogakkoPostService;
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
    @DisplayName("모각코 방 생성 테스트")
    class CreateMogakko {
        @DisplayName("모각코 방 생성 성공 테스트")
        @Test
        void createMogakko_success() throws Exception {
            // given
            MogakkoRoomCreateRequestDto requestDto = MogakkoRoomCreateRequestDto.builder()
                    .title("코딩킹")
                    .language(LanguageEnum.JAVA)
                    .lat(34.33333)
                    .lon(125.333344)
                    .maxMembers(4L)
                    .neighborhood("가남읍")
                    .isOpened(false)
                    .password("3232")
                    .build();

            MogakkoRoomCreateResponseDto responseDto = MogakkoRoomCreateResponseDto.builder()
                    .sessionId("newSessionId")
                    .build();

            when(mogakkoServiceUtilMethod.createNewToken(member)).thenReturn(responseDto);
            when(badWordFiltering.checkBadWord(requestDto.getTitle())).thenReturn(requestDto.getTitle());

            // when
            ResponseEntity<Message> response = mogakkoPostService.createMogakko(requestDto, member);

            // then
            MogakkoRoom mogakkoRoom = (MogakkoRoom) response.getBody().getData();
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("모각코방 생성 성공", response.getBody().getMessage());
            assertEquals(mogakkoRoom.getSessionId(), "newSessionId");
            assertEquals(mogakkoRoom.getTitle(), "코딩킹");
        }
    }

    @Nested
    @DisplayName("모각코 방 입장 테스트")
    class EnterMogakko{
        @DisplayName("모각코 방 입장 성공 테스트")
        @Test
        void enterMogakko_success() throws OpenViduJavaClientException, OpenViduHttpException {
            // given
            MogakkoRoomEnterDataRequestDto requestDto = MogakkoRoomEnterDataRequestDto.builder().password("3232").build();

            when(mogakkoRoomRepository.findBySessionId("sessionId")).thenReturn(Optional.of(mogakkoRoom));
            when(mogakkoRoomMembersRepository.findByMemberIdAndMogakkoRoomAndIsEntered(member.getId(), mogakkoRoom, true)).thenReturn(Optional.empty());
            when(passwordEncoder.matches(requestDto.getPassword(), mogakkoRoom.getPassword())).thenReturn(true);
            when(mogakkoRoomMembersRepository.findByMogakkoRoomAndMemberId(mogakkoRoom, member.getId())).thenReturn(Optional.empty());
            when(mogakkoServiceUtilMethod.enterRoomCreateSession(member, mogakkoRoom.getSessionId())).thenReturn("ws://sessionId?=sessionId&token?=3dnf23k");

            // when
            ResponseEntity<Message> response = mogakkoPostService.enterMogakko("sessionId", requestDto, member);

            // then
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody().getMessage(), "모각코방 입장 성공");
            assertEquals(response.getBody().getData(), "ws://sessionId?=sessionId&token?=3dnf23k");
        }

        @DisplayName("방장 모각코 방 입장 성공 테스트")
        @Test
        void enterMogakko_successEnterMogakkoRoomMaster() throws OpenViduJavaClientException, OpenViduHttpException {
            // given
            MogakkoRoom mogakkoRoom = MogakkoRoom.builder()
                    .sessionId("sessionId")
                    .title("코딩킹")
                    .language(LanguageEnum.JAVA)
                    .lat(34.33333)
                    .lon(125.333344)
                    .maxMembers(4L)
                    .neighborhood("가남읍")
                    .isOpened(false)
                    .cntMembers(2L)
                    .masterMemberId(1L)
                    .build();

            when(mogakkoRoomRepository.findBySessionId("sessionId")).thenReturn(Optional.of(mogakkoRoom));
            when(mogakkoRoomMembersRepository.findByMemberIdAndMogakkoRoomAndIsEntered(member.getId(), mogakkoRoom, true)).thenReturn(Optional.empty());
            when(mogakkoRoomMembersRepository.findByMogakkoRoomAndMemberId(mogakkoRoom, member.getId())).thenReturn(Optional.empty());
            when(mogakkoServiceUtilMethod.enterRoomCreateSession(member, mogakkoRoom.getSessionId())).thenReturn("ws://sessionId?=sessionId&token?=3dnf23k");

            // when
            ResponseEntity<Message> response = mogakkoPostService.enterMogakko("sessionId", null, member);

            // then
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody().getMessage(), "모각코방 입장 성공");
            assertEquals(response.getBody().getData(), "ws://sessionId?=sessionId&token?=3dnf23k");
        }

        @DisplayName("비밀번호 없는 모각코 방 입장 성공 테스트")
        @Test
        void enterMogakko_successNotPassword() throws OpenViduJavaClientException, OpenViduHttpException {
            // given
            MogakkoRoom mogakkoRoom = MogakkoRoom.builder()
                    .sessionId("sessionId")
                    .title("코딩킹")
                    .language(LanguageEnum.JAVA)
                    .lat(34.33333)
                    .lon(125.333344)
                    .maxMembers(4L)
                    .neighborhood("가남읍")
                    .isOpened(true)
                    .cntMembers(2L)
                    .masterMemberId(3L)
                    .build();

            when(mogakkoRoomRepository.findBySessionId("sessionId")).thenReturn(Optional.of(mogakkoRoom));
            when(mogakkoRoomMembersRepository.findByMemberIdAndMogakkoRoomAndIsEntered(member.getId(), mogakkoRoom, true)).thenReturn(Optional.empty());
            when(mogakkoRoomMembersRepository.findByMogakkoRoomAndMemberId(mogakkoRoom, member.getId())).thenReturn(Optional.empty());
            when(mogakkoServiceUtilMethod.enterRoomCreateSession(member, mogakkoRoom.getSessionId())).thenReturn("ws://sessionId?=sessionId&token?=3dnf23k");

            // when
            ResponseEntity<Message> response = mogakkoPostService.enterMogakko("sessionId", null, member);

            // then
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody().getMessage(), "모각코방 입장 성공");
            assertEquals(response.getBody().getData(), "ws://sessionId?=sessionId&token?=3dnf23k");
        }

        @DisplayName("존재하지 않는 모각코 방 입장 테스트")
        @Test
        void enterMogakko_noMogakkoRoom() {
            // given
            MogakkoRoomEnterDataRequestDto requestDto = MogakkoRoomEnterDataRequestDto.builder().password("3232").build();

            when(mogakkoRoomRepository.findBySessionId("sessionId")).thenReturn(Optional.empty());

            // when & then
            CustomException customException = assertThrows(CustomException.class, ()-> mogakkoPostService.enterMogakko("sessionId", requestDto, member));
            assertEquals(customException.getErrorCode(), MOGAKKO_NOT_FOUND);
        }

        @DisplayName("이미 입장한 모각코 방 입장 테스트")
        @Test
        void enterMogakko_alreadyJoin() {
            // given
            MogakkoRoomEnterDataRequestDto requestDto = MogakkoRoomEnterDataRequestDto.builder().password("3232").build();

            when(mogakkoRoomRepository.findBySessionId("sessionId")).thenReturn(Optional.of(mogakkoRoom));
            when(mogakkoRoomMembersRepository.findByMemberIdAndMogakkoRoomAndIsEntered(member.getId(), mogakkoRoom, true)).thenReturn(Optional.of(new MogakkoRoomMembers()));

            // when & then
            CustomException customException = assertThrows(CustomException.class, ()-> mogakkoPostService.enterMogakko("sessionId", requestDto, member));
            assertEquals(customException.getErrorCode(), ALREADY_ENTER_MEMBER);
        }

        @DisplayName("인원이 다 찬 모각코 방 입장 테스트")
        @Test
        void enterMogakko_joinMaxMembersMogakkoRoom() {
            // given
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
                    .cntMembers(4L)
                    .masterMemberId(3L)
                    .build();
            MogakkoRoomEnterDataRequestDto requestDto = MogakkoRoomEnterDataRequestDto.builder().password("3232").build();

            when(mogakkoRoomRepository.findBySessionId("sessionId")).thenReturn(Optional.of(mogakkoRoom));
            when(mogakkoRoomMembersRepository.findByMemberIdAndMogakkoRoomAndIsEntered(member.getId(), mogakkoRoom, true)).thenReturn(Optional.empty());

            // when & then
            CustomException customException = assertThrows(CustomException.class, ()-> mogakkoPostService.enterMogakko("sessionId", requestDto, member));
            assertEquals(customException.getErrorCode(), MOGAKKO_IS_FULL);
        }

        @DisplayName("비밀번호 입력 안한 후 모각코 방 입장 테스트 1")
        @Test
        void enterMogakko_notInputPassword1() {
            // given
            when(mogakkoRoomRepository.findBySessionId("sessionId")).thenReturn(Optional.of(mogakkoRoom));
            when(mogakkoRoomMembersRepository.findByMemberIdAndMogakkoRoomAndIsEntered(member.getId(), mogakkoRoom, true)).thenReturn(Optional.empty());


            // when & then
            CustomException customException = assertThrows(CustomException.class, ()-> mogakkoPostService.enterMogakko("sessionId", null, member));
            assertEquals(customException.getErrorCode(), PLZ_INPUT_PASSWORD);
        }

        @DisplayName("비밀번호 입력 안한 후 모각코 방 입장 테스트 2")
        @Test
        void enterMogakko_notInputPassword2() {
            // given
            MogakkoRoomEnterDataRequestDto requestDto = MogakkoRoomEnterDataRequestDto.builder().build();

            when(mogakkoRoomRepository.findBySessionId("sessionId")).thenReturn(Optional.of(mogakkoRoom));
            when(mogakkoRoomMembersRepository.findByMemberIdAndMogakkoRoomAndIsEntered(member.getId(), mogakkoRoom, true)).thenReturn(Optional.empty());


            // when & then
            CustomException customException = assertThrows(CustomException.class, ()-> mogakkoPostService.enterMogakko("sessionId", requestDto, member));
            assertEquals(customException.getErrorCode(), PLZ_INPUT_PASSWORD);
        }
        @DisplayName("비밀번호 입력 안한 후 모각코 방 입장 테스트 3")
        @Test
        void enterMogakko_notInputPassword3() {
            // given
            MogakkoRoomEnterDataRequestDto requestDto = MogakkoRoomEnterDataRequestDto.builder().password("").build();

            when(mogakkoRoomRepository.findBySessionId("sessionId")).thenReturn(Optional.of(mogakkoRoom));
            when(mogakkoRoomMembersRepository.findByMemberIdAndMogakkoRoomAndIsEntered(member.getId(), mogakkoRoom, true)).thenReturn(Optional.empty());


            // when & then
            CustomException customException = assertThrows(CustomException.class, ()-> mogakkoPostService.enterMogakko("sessionId", requestDto, member));
            assertEquals(customException.getErrorCode(), PLZ_INPUT_PASSWORD);
        }

        @DisplayName("비밀번호 검증 실패 모각코 방 입장 테스트")
        @Test
        void enterMogakko_wrongPassword() {
            // given

            MogakkoRoomEnterDataRequestDto requestDto = MogakkoRoomEnterDataRequestDto.builder().password("password").build();

            when(mogakkoRoomRepository.findBySessionId("sessionId")).thenReturn(Optional.of(mogakkoRoom));
            when(mogakkoRoomMembersRepository.findByMemberIdAndMogakkoRoomAndIsEntered(member.getId(), mogakkoRoom, true)).thenReturn(Optional.empty());
            when(passwordEncoder.matches(requestDto.getPassword(), mogakkoRoom.getPassword())).thenReturn(false);

            // when & then
            CustomException customException = assertThrows(CustomException.class, ()-> mogakkoPostService.enterMogakko("sessionId", requestDto, member));
            assertEquals(customException.getErrorCode(), INVALID_PASSWORD);
        }

        @DisplayName("모각코 방 재입장 테스트")
        @Test
        void enterMogakko_reEnterMember() throws OpenViduJavaClientException, OpenViduHttpException {
            // given

            MogakkoRoomEnterDataRequestDto requestDto = MogakkoRoomEnterDataRequestDto.builder().password("3232").build();
            MogakkoRoomMembers mogakkoRoomMembers = MogakkoRoomMembers.builder()
                    .memberId(1L)
                    .enterRoomToken("ws://sessionId?=sessionId&token?=3dnf23k")
                    .isEntered(false)
                    .build();

            when(mogakkoRoomRepository.findBySessionId("sessionId")).thenReturn(Optional.of(mogakkoRoom));
            when(mogakkoRoomMembersRepository.findByMemberIdAndMogakkoRoomAndIsEntered(member.getId(), mogakkoRoom, true)).thenReturn(Optional.empty());
            when(passwordEncoder.matches(requestDto.getPassword(), mogakkoRoom.getPassword())).thenReturn(true);
            when(mogakkoRoomMembersRepository.findByMogakkoRoomAndMemberId(mogakkoRoom, member.getId())).thenReturn(Optional.of(mogakkoRoomMembers));
            when(mogakkoServiceUtilMethod.enterRoomCreateSession(member, mogakkoRoom.getSessionId())).thenReturn("ws://sessionId?=sessionId&token?=3dnf23k");

            // when
            ResponseEntity<Message> response = mogakkoPostService.enterMogakko("sessionId", requestDto, member);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody().getMessage(), "모각코방 입장 성공");
            assertEquals(response.getBody().getData(), "ws://sessionId?=sessionId&token?=3dnf23k");
            assertEquals(mogakkoRoomMembers.isEntered(), true);
        }

    }
}
