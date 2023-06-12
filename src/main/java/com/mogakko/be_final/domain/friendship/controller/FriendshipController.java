package com.mogakko.be_final.domain.friendship.controller;

import com.mogakko.be_final.domain.friendship.dto.DeleteFriendRequestDto;
import com.mogakko.be_final.domain.friendship.dto.DetermineRequestDto;
import com.mogakko.be_final.domain.friendship.dto.FriendRequestByCodeDto;
import com.mogakko.be_final.domain.friendship.dto.FriendRequestDto;
import com.mogakko.be_final.domain.friendship.service.FriendshipSearchService;
import com.mogakko.be_final.domain.friendship.service.FriendshipService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "친구기능 관련 API", description = "친구기능 관련 API 입니다.")
@RestController
@RequestMapping("/friendship/requests")
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipService friendshipService;
    private final FriendshipSearchService friendshipSearchService;

    @PostMapping
    @Operation(summary = "친구 요청 API", description = "친구 요청을 보내는 메서드입니다. 수신자의 nickname을 보내주시면 요청이 완료됩니다.")
    public ResponseEntity<Message> friendRequest(@RequestBody FriendRequestDto friendRequestDto,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails){
        return friendshipService.friendRequest(friendRequestDto.getRequestReceiverNickname(), userDetails.getMember());
    }
    @PostMapping("/determine")
    @Operation(summary = "친구 요청 결정 API", description = "친구 요청을 결정하는 메서드입니다. determineRequest 의 값이 true면 수락, false면 거절 입니다.")
    public ResponseEntity<Message> determineRequest(@RequestBody DetermineRequestDto determineRequestDto,
                                                    @AuthenticationPrincipal UserDetailsImpl userDetails){
        return friendshipService.determineRequest(determineRequestDto, userDetails.getMember());
    }

    @PostMapping("/delete")
    @Operation(summary = "친구 삭제 API", description = "사용자의 친구를 삭제하는 메서드입니다.")
    public ResponseEntity<Message> deleteFriend(@RequestBody DeleteFriendRequestDto deleteFriendRequestDto,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails){
        return friendshipService.deleteFriend(deleteFriendRequestDto, userDetails.getMember());
    }

    @GetMapping("/accepted")
    @Operation(summary = "친구목록 조회 API", description = "사용자의 친구 목록을 조회하는 메서드입니다.")
    public ResponseEntity<Message> getMyFriend(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return friendshipSearchService.getMyFriend(userDetails.getMember());
    }

    @GetMapping("/pending")
    @Operation(summary = "받은 친구요청 조회 API", description = "사용자의 친구 요청 목록을 조회하는 메서드입니다.")
    public ResponseEntity<Message> getMyFriendRequest(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return friendshipSearchService.getMyFriendRequest(userDetails.getMember());
    }

    @PostMapping("/code")
    @Operation(summary = "친구코드로 친구 요청 API", description = "친구 요청을 보내는 메서드입니다. 수신자의 friendCode 를 보내면 요청이 완료됩니다.")
    public ResponseEntity<Message> friendRequestByCode(@RequestBody FriendRequestByCodeDto friendRequestByCodeDto,
                                                       @AuthenticationPrincipal UserDetailsImpl userDetails){
        return friendshipService.friendRequestByCode(friendRequestByCodeDto.getRequestReceiverFriendCode(), userDetails.getMember());
    }
}
