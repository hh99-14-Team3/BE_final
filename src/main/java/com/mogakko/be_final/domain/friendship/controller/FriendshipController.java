package com.mogakko.be_final.domain.friendship.controller;


import com.mogakko.be_final.domain.friendship.dto.FriendRequestDto;
import com.mogakko.be_final.domain.friendship.service.FriendshipService;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipService friendshipService;

    @PostMapping("/friendship/Request")
    public ResponseEntity<Message> friendRequest(@RequestBody FriendRequestDto friendRequestDto){
        return friendshipService.friendRequest(friendRequestDto);
    }
    @PostMapping("/friendship/Request/determine")
    public ResponseEntity<Message> determineRequest(@RequestParam String authCode){
        return null;
    }

    @GetMapping("/friendship/myFriend")
    public ResponseEntity<Message> getMyFriend(){
        return null;
    }


}
