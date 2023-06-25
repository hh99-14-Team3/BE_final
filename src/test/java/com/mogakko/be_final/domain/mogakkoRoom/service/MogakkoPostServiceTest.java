package com.mogakko.be_final.domain.mogakkoRoom.service;

import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.entity.SocialType;
import com.mogakko.be_final.domain.mogakkoRoom.dto.request.MogakkoRoomCreateRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.dto.response.MogakkoRoomCreateResponseDto;
import com.mogakko.be_final.domain.mogakkoRoom.entity.LanguageEnum;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoom;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomRepository;
import com.mogakko.be_final.domain.mogakkoRoom.util.MogakkoServiceUtilMethod;
import com.mogakko.be_final.util.BadWordFiltering;
import com.mogakko.be_final.util.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @DisplayName("모각코 방 생성 성공 TEST")
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
