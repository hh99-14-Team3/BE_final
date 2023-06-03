package com.mogakko.be_final.domain.friendship.controller;

import com.mogakko.be_final.domain.friendship.dto.DeleteFriendRequestDto;
import com.mogakko.be_final.domain.friendship.dto.DetermineRequestDto;
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
    @Operation(summary = "친구 요청 API", description = " receiver 의 nickname 을 보내주시면 요청이 완료됩니다.")
    public ResponseEntity<Message> friendRequest(@RequestBody FriendRequestDto friendRequestDto,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails){
        return friendshipService.friendRequest(friendRequestDto, userDetails);
    }
    @PostMapping("/determine")
    @Operation(summary = "친구요청의 수락 또는 거절 API", description = "determineRequest 의 값이 true면 수락, false면 거절 입니다.")
    public ResponseEntity<Message> determineRequest(@RequestBody DetermineRequestDto determineRequestDto){
        return friendshipService.determineRequest(determineRequestDto);
    }

    @GetMapping("/accepted")
    @Operation(summary = "사용자의 친구목록 조회 API", description = "해당주소에 토큰을 보내면 친구 목록이 조회됩니다.")
    public ResponseEntity<Message> getMyFriend(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return friendshipSearchService.getMyFriend(userDetails);
    }

    @GetMapping("/pending")
    @Operation(summary = "사용자가 수신한 친구요청 조회 API", description = "해당주소에 토큰을 보내면 친구 요청 목록이 조회됩니다.")
    public ResponseEntity<Message> getMyFriendRequest(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return friendshipSearchService.getMyFriendRequest(userDetails);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "친구 삭제 API", description = "친구 삭제 API입니다. 요청 보내면 삭제 됩니다.")
    public ResponseEntity<Message> deleteFriend(@RequestBody DeleteFriendRequestDto deleteFriendRequestDto,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails){
        return friendshipService.deleteFriend(deleteFriendRequestDto, userDetails);
    }
}
