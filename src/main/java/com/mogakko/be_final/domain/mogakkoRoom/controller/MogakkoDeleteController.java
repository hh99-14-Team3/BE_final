package com.mogakko.be_final.domain.mogakkoRoom.controller;

import com.mogakko.be_final.domain.mogakkoRoom.service.MogakkoDeleteService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "모각코방 DELETE 요청 관련 API", description = "모각코방 관련 DELETE 요청 API 입니다.")
public class MogakkoDeleteController {
    private final MogakkoDeleteService mogakkoDeleteService;

    @Operation(summary = "모각코 방 퇴장 API", description = "퇴장하기 버튼 눌렀을때 모각코 방이 퇴장되는 메서드입니다.")
    @DeleteMapping("/mogakko/{sessionId}")
    public ResponseEntity<Message> outClickMogakko(@PathVariable(name = "sessionId") String sessionId,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mogakkoDeleteService.outMogakko(sessionId, userDetails.getMember());
    }
}
