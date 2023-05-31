package com.mogakko.be_final.domain.friendship.controller;

import com.mogakko.be_final.domain.friendship.dto.DetermineRequestDto;
import com.mogakko.be_final.domain.friendship.dto.FriendRequestDto;
import com.mogakko.be_final.domain.friendship.service.FriendshipSearchService;
import com.mogakko.be_final.domain.friendship.service.FriendshipService;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipService friendshipService;
    private final FriendshipSearchService friendshipSearchService;

    @PostMapping("/friendship/request")
    public ResponseEntity<Message> friendRequest(@RequestBody FriendRequestDto friendRequestDto){
        return friendshipService.friendRequest(friendRequestDto);
    }
    @PostMapping("/friendship/request/determine")
    public ResponseEntity<Message> determineRequest(@RequestBody DetermineRequestDto determineRequestDto){
        return friendshipService.determineRequest(determineRequestDto);
    }

    @GetMapping("/friendship/myFriend/{memberId}")
    public ResponseEntity<Message> getMyFriend(@PathVariable Long memberId){

        return friendshipSearchService.getMyFriend(memberId);
    }


}
