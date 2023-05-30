package com.mogakko.be_final.domain.friendship.controller;


import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FriendshipController {

    @PostMapping("/members/friendRequest")
    public ResponseEntity<Message> friendRequest(@RequestParam String email){
        return null;
    }
    @PostMapping("/members/friendship/acceptRequest")
    public ResponseEntity<Message> acceptRequest(@RequestParam String authCode){
        return null;
    }

    @PostMapping("/members/friendship/refuseRequest")
    public ResponseEntity<Message> refuseRequest(@RequestParam String authCode){
        return null;
    }

    @GetMapping("/members/freindship/myFriend")
    public ResponseEntity<Message> getMyFriend(){
        return null;
    }


}
