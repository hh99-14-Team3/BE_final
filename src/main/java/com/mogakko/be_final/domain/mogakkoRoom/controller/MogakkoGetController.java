package com.mogakko.be_final.domain.mogakkoRoom.controller;

import com.mogakko.be_final.domain.mogakkoRoom.service.MogakkoGetService;
import com.mogakko.be_final.util.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "모각코방 GET 요청 관련 API", description = "모각코방 관련 GET 요청 API 입니다.")
public class MogakkoGetController {

    private final MogakkoGetService mogakkoGetService;

    @Operation(summary = "인기 지역 모각코 조회 API", description = "인기있는 지역의 모각코를 조회하는 메서드입니다.")
    @GetMapping("/mogakkos/top")
    public ResponseEntity<Message> topMogakko() {
        return mogakkoGetService.topMogakko();
    }
}
