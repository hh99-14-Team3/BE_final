package com.mogakko.be_final.domain.members.service;

import com.mogakko.be_final.S3.S3Uploader;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static com.mogakko.be_final.exception.ErrorCode.DUPLICATE_NICKNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class MembersPutServiceTest {
    @Mock
    MembersRepository membersRepository;
    @Mock
    S3Uploader s3Uploader;
    @InjectMocks
    MembersPutService membersPutService;

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

    @Nested
    @DisplayName("프로필 업데이트 테스트")
    class profileUpdate {
        @DisplayName("프로필 업데이트 성공 테스트")
        @Test
        void profileUpdate_success() throws IOException {
            // given
            MultipartFile imageFile = mock(MultipartFile.class);
            String nickname = "been1118";

            when(membersRepository.findByNickname(nickname)).thenReturn(Optional.empty());
            when(s3Uploader.uploadFile(imageFile)).thenReturn("new-profile-image");

            // when
            ResponseEntity<Message> response = membersPutService.profileUpdate(imageFile, nickname, member);

            // then
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody().getMessage(), "프로필 정보 변경 성공");
            assertEquals(member.getProfileImage(), "new-profile-image");
            assertEquals(member.getNickname(), nickname);
            assertEquals(member.getMemberStatusCode(), MemberStatusCode.NORMAL);
        }

        @DisplayName("프로필 업데이트 MemberStatus Code Not Change 테스트")
        @Test
        void profileUpdate_notChangeStatueCode() throws IOException {
            // given
            MultipartFile imageFile = mock(MultipartFile.class);
            String nickname = "been1118";
            member.changeMemberStatusCode(MemberStatusCode.EMOTICON);

            when(membersRepository.findByNickname(nickname)).thenReturn(Optional.empty());
            when(s3Uploader.uploadFile(imageFile)).thenReturn("new-profile-image");

            // when
            ResponseEntity<Message> response = membersPutService.profileUpdate(imageFile, nickname, member);

            // then
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody().getMessage(), "프로필 정보 변경 성공");
            assertEquals(member.getProfileImage(), "new-profile-image");
            assertEquals(member.getNickname(), nickname);
            assertEquals(member.getMemberStatusCode(), MemberStatusCode.EMOTICON);
        }

        @DisplayName("프로필 이미지 단일 업데이트 성공 테스트")
        @Test
        void profileUpdate_notChangeNickname() throws IOException {
            // given
            MultipartFile imageFile = mock(MultipartFile.class);

            when(s3Uploader.uploadFile(imageFile)).thenReturn("new-profile-image");

            // when
            ResponseEntity<Message> response = membersPutService.profileUpdate(imageFile, null, member);

            // then
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody().getMessage(), "프로필 정보 변경 성공");
            assertEquals(member.getProfileImage(), "new-profile-image");
            assertEquals(member.getNickname(), "nickname");
            assertEquals(member.getMemberStatusCode(), MemberStatusCode.NORMAL);
        }

        @DisplayName("닉네임 단일 업데이트 성공 테스트")
        @Test
        void profileUpdate_notChangeProfileImage() throws IOException {
            // given
            String nickname = "been1118";
            when(membersRepository.findByNickname(nickname)).thenReturn(Optional.empty());

            // when
            ResponseEntity<Message> response = membersPutService.profileUpdate(null, nickname, member);

            // then
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody().getMessage(), "프로필 정보 변경 성공");
            assertEquals(member.getProfileImage(), "https://source.boringavatars.com/beam/120/$" + "nickname" + "?colors=00F0FF,172435,394254,EAEBED,F9F9FA");
            assertEquals(member.getNickname(), "been1118");
            assertEquals(member.getMemberStatusCode(), MemberStatusCode.NORMAL);
        }

        @DisplayName("닉네임 중복 테스트")
        @Test
        void profileUpdate_duplication() throws IOException {
            // given
            String nickname = "been1118";
            when(membersRepository.findByNickname(nickname)).thenReturn(Optional.of(new Members()));

            // when & then
            CustomException exception = assertThrows(CustomException.class, () -> membersPutService.profileUpdate(null, nickname, member));
            assertEquals(exception.getErrorCode(), DUPLICATE_NICKNAME);
        }
    }

    @Test
    void profileDelete() {
    }

    @Test
    void tutorialCheck() {
    }
}