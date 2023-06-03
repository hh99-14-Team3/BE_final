package com.mogakko.be_final.domain.sse.service;

import com.amazonaws.Response;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationSearchService {
    public ResponseEntity<Message> getMyNotification(UserDetailsImpl userDetails){

        return null;


    }
}
