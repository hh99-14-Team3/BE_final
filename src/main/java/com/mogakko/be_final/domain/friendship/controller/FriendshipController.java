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
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipService friendshipService;
    private final FriendshipSearchService friendshipSearchService;

    @PostMapping("/friendship/request")
    @Operation(summary = "친구 요청 API", description = "sender와 receiver는 이메일 주소로 받습니다. 추후 변경가능")
    public ResponseEntity<Message> friendRequest(@RequestBody FriendRequestDto friendRequestDto){
        return friendshipService.friendRequest(friendRequestDto);
    }
    @PostMapping("/friendship/request/determine")
    @Operation(summary = "친구 요청의 수락 또는 거절 API", description = "determineRequest 의 값이 true면 수락, false면 거절 입니다.")
    public ResponseEntity<Message> determineRequest(@RequestBody DetermineRequestDto determineRequestDto){
        return friendshipService.determineRequest(determineRequestDto);
    }

    @GetMapping("/friendship/myFriend")
    @Operation(summary = "특정 사용자의 친구 목록 조회 API", description = "URL의 memberId 파라미터를 통해 식별됩니다. 추후 변경가능")
    public ResponseEntity<Message> getMyFriend(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return friendshipSearchService.getMyFriend(userDetails.getMember().getId());
    }

    @PostMapping("/friendship/request/delete")
    @Operation(summary = "친구 삭제 API", description = "친구 삭제 API입니다. 요청 보내면 삭제 됩니다.")
    public ResponseEntity<Message> deleteFreind(@RequestBody DeleteFriendRequestDto deleteFriendRequestDto){
        return friendshipService.deleteFriend(deleteFriendRequestDto);
    }
}
