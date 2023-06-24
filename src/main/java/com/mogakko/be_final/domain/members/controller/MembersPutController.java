package com.mogakko.be_final.domain.members.controller;

import com.mogakko.be_final.domain.members.service.MembersPutService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "회원 관련 PUT 요청 API", description = "회원 관련 PUT 요청 API 입니다.")
public class MembersPutController {

    private final MembersPutService membersPutService;

    @Operation(summary = "마이페이지 수정 API", description = "마이페이지에서 정보(프로필 사진, 닉네임)를 변경하는 메서드입니다.")
    @PutMapping(value = "/mypage", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Message> profileUpdate(@RequestPart(value = "imageFile", required = false) MultipartFile image,
                                                 @RequestPart(value = "nickname", required = false) String nickname,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return membersPutService.profileUpdate(image, nickname, userDetails.getMember());
    }

    @Operation(summary = "마이페이지 프로필사진 삭제 API", description = "마이페이지에서 프로필 사진을 삭제하는 메서드입니다.")
    @PutMapping("/mypage/delete")
    public ResponseEntity<Message> profileDelete(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return membersPutService.profileDelete(userDetails.getMember());
    }

    @Operation(summary = "튜토리얼 체크 메서드 API", description = "유저가 튜토리얼을 확인했는지 체크하는 메서드입니다.")
    @PutMapping("/tutorial-check")
    public ResponseEntity<Message> tutorialCheck(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return membersPutService.tutorialCheck(userDetails.getMember());
    }
}
