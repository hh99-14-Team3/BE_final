package com.mogakko.be_final.domain.friendship.controller;

import com.mogakko.be_final.domain.friendship.dto.request.DeleteFriendRequestDto;
import com.mogakko.be_final.domain.friendship.dto.request.DetermineRequestDto;
import com.mogakko.be_final.domain.friendship.dto.request.FriendRequestByCodeDto;
import com.mogakko.be_final.domain.friendship.dto.request.FriendRequestDto;
import com.mogakko.be_final.domain.friendship.service.FriendshipPostService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "친구기능 POST 요청 관련 API", description = "친구기능 관련 POST 요청 API 입니다.")
@RestController
@RequestMapping("/friendship/requests")
@RequiredArgsConstructor
public class FriendshipPostController {
    private final FriendshipPostService friendshipPostService;

    @Operation(summary = "친구 요청 API", description = "친구 요청을 보내는 메서드입니다. 수신자의 nickname을 보내주시면 요청이 완료됩니다.")
    @PostMapping
    public ResponseEntity<Message> friendRequest(@RequestBody FriendRequestDto friendRequestDto,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return friendshipPostService.friendRequest(friendRequestDto.getRequestReceiverNickname(), userDetails.getMember());
    }

    @Operation(summary = "친구 요청 API", description = "친구 요청을 보내는 메서드입니다. 수신자의 friendCode 를 보내면 요청이 완료됩니다.")
    @PostMapping("/code")
    public ResponseEntity<Message> friendRequestByCode(@RequestBody FriendRequestByCodeDto friendRequestByCodeDto,
                                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return friendshipPostService.friendRequestByCode(friendRequestByCodeDto.getRequestReceiverFriendCode(), userDetails.getMember());
    }

    @Operation(summary = "친구 요청 결정 API", description = "친구 요청을 결정하는 메서드입니다. determineRequest 의 값이 true면 수락, false면 거절 입니다.")
    @PostMapping("/determine")
    public ResponseEntity<Message> determineRequest(@RequestBody DetermineRequestDto determineRequestDto,
                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return friendshipPostService.determineRequest(determineRequestDto, userDetails.getMember());
    }

    @Operation(summary = "친구 삭제 API", description = "사용자의 친구를 삭제하는 메서드입니다.")
    @PostMapping("/delete")
    public ResponseEntity<Message> deleteFriend(@RequestBody DeleteFriendRequestDto deleteFriendRequestDto,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return friendshipPostService.deleteFriend(deleteFriendRequestDto, userDetails.getMember());
    }
}
