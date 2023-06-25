package com.mogakko.be_final.domain.mogakkoRoom.controller;

import com.mogakko.be_final.domain.mogakkoRoom.dto.request.MogakkoTimerRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.service.MogakkoPutService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "모각코방 PUT 요청 관련 API", description = "모각코방 관련 PUT 요청 API 입니다.")
public class MogakkoPutController {

    private final MogakkoPutService mogakkoPutService;

    @Operation(summary = "모각코 타이머 API", description = "모각코 시간을 측정하는 메서드입니다.")
    @PutMapping("/mogakko/timer")
    public ResponseEntity<Message> mogakkoTimer(@RequestBody MogakkoTimerRequestDto mogakkoTimerRequestDto,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mogakkoPutService.mogakkoTimer(mogakkoTimerRequestDto, userDetails.getMember());
    }
}
