package com.mogakko.be_final.domain.friendship.controller;

import com.mogakko.be_final.domain.friendship.service.FriendshipGetService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "친구기능 관련 GET 요청 API", description = "친구기능 관련 GET 요청 API 입니다.")
@RestController
@RequestMapping("/friendship/requests")
@RequiredArgsConstructor
public class FriendshipGetController {
    private final FriendshipGetService friendshipGetService;

    @Operation(summary = "친구목록 조회 API", description = "사용자의 친구 목록을 조회하는 메서드입니다.")
    @GetMapping("/accepted")
    public ResponseEntity<Message> getMyFriend(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return friendshipGetService.getMyFriend(userDetails.getMember());
    }

    @Operation(summary = "받은 친구요청 조회 API", description = "사용자의 친구 요청 목록을 조회하는 메서드입니다.")
    @GetMapping("/pending")
    public ResponseEntity<Message> getMyFriendRequest(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return friendshipGetService.getMyFriendRequest(userDetails.getMember());
    }
}
