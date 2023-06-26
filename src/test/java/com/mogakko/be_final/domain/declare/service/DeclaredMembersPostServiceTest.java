package com.mogakko.be_final.domain.declare.service;

import com.mogakko.be_final.domain.declare.dto.request.DeclareRequestDto;
import com.mogakko.be_final.domain.declare.entity.DeclaredMembers;
import com.mogakko.be_final.domain.declare.entity.DeclaredReason;
import com.mogakko.be_final.domain.declare.repository.DeclaredMembersRepository;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.util.MembersServiceUtilMethod;
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

import static com.mogakko.be_final.exception.ErrorCode.CANNOT_REQUEST;
import static com.mogakko.be_final.exception.ErrorCode.PLZ_INPUT_REASON_OF_REPORT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@DisplayName("Declared Members Service - [POST] 테스트")
@ExtendWith({MockitoExtension.class})
class DeclaredMembersPostServiceTest {

    @Mock
    DeclaredMembersRepository declaredMembersRepository;
    @Mock
    MembersServiceUtilMethod membersServiceUtilMethod;
    @InjectMocks
    DeclaredMembersPostService declaredMembersPostService;

    Members reporter = Members.builder()
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

    Members declared = Members.builder()
            .email("test1@example.com")
            .nickname("namenick")
            .password("password1!")
            .role(Role.USER)
            .codingTem(36.5)
            .mogakkoTotalTime(0L)
            .memberStatusCode(MemberStatusCode.BASIC)
            .profileImage("https://source.boringavatars.com/beam/120/$" + "nickname" + "?colors=00F0FF,172435,394254,EAEBED,F9F9FA")
            .friendCode(123456)
            .isTutorialCheck(false)
            .build();

    @DisplayName("[POST] 회원 신고 성공 테스트")
    @Test
    void declareMember() {
        DeclareRequestDto declareRequestDto = DeclareRequestDto.builder()
                .declaredNickname(declared.getNickname())
                .declaredReason(DeclaredReason.ABUSE)
                .reason(null)
                .build();
        when(membersServiceUtilMethod.findMemberByNickname(declared.getNickname())).thenReturn(declared);
        when(declaredMembersRepository.save(any(DeclaredMembers.class))).thenReturn(null);

        ResponseEntity<Message> response = declaredMembersPostService.declareMember(declareRequestDto, reporter);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getMessage(), "멤버 신고 성공");
    }


    @DisplayName("[POST] 회원 신고 성공 테스트 - 기타 사유 입력함")
    @Test
    void declareMember_doWriteReason() {
        DeclareRequestDto declareRequestDto = DeclareRequestDto.builder()
                .declaredNickname(declared.getNickname())
                .declaredReason(DeclaredReason.ETC)
                .reason("")
                .build();

        when(membersServiceUtilMethod.findMemberByNickname(declared.getNickname())).thenReturn(declared);

        CustomException customException = assertThrows(CustomException.class, () ->
                declaredMembersPostService.declareMember(declareRequestDto, reporter));

        assertEquals(PLZ_INPUT_REASON_OF_REPORT, customException.getErrorCode());
        assertEquals(declareRequestDto.getDeclaredReason(), DeclaredReason.ETC);
        assertEquals(declareRequestDto.getReason(), "");
    }

    @DisplayName("[POST] 회원 신고 실패 테스트 - 기타 사유 입력 안함")
    @Test
    void declareMember_doNotWriteReason() {
        DeclareRequestDto declareRequestDto = DeclareRequestDto.builder()
                .declaredNickname(declared.getNickname())
                .declaredReason(DeclaredReason.ETC)
                .reason("신고 사유 입니다.")
                .build();
        when(membersServiceUtilMethod.findMemberByNickname(declared.getNickname())).thenReturn(declared);
        when(declaredMembersRepository.save(any(DeclaredMembers.class))).thenReturn(null);

        ResponseEntity<Message> response = declaredMembersPostService.declareMember(declareRequestDto, reporter);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getMessage(), "멤버 신고 성공");
    }


    @DisplayName("[POST] 회원 신고 실패 테스트 - 자기 자신은 신고할 수 없음")
    @Test
    void declareMember_cannotReportMyself() {
        DeclareRequestDto declareRequestDto = DeclareRequestDto.builder()
                .declaredNickname(reporter.getNickname())
                .declaredReason(DeclaredReason.ETC)
                .reason("")
                .build();
        when(membersServiceUtilMethod.findMemberByNickname(reporter.getNickname())).thenReturn(reporter);

        CustomException customException = assertThrows(CustomException.class, () ->
                declaredMembersPostService.declareMember(declareRequestDto, reporter));

        assertEquals(CANNOT_REQUEST, customException.getErrorCode());
    }
}
