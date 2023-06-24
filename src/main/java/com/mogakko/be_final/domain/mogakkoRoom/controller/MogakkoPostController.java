package com.mogakko.be_final.domain.mogakkoRoom.controller;

import com.mogakko.be_final.domain.mogakkoRoom.dto.request.Mogakko12kmRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.dto.request.MogakkoRoomCreateRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.dto.request.MogakkoRoomEnterDataRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.service.MogakkPostService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Tag(name = "모각코방 POST 요청 관련 API", description = "모각코방 관련 POST 요청 API 입니다.")
public class MogakkoPostController {

    private final MogakkPostService mogakkPostService;

    @Operation(summary = "모각코 방 생성 API", description = "모각코 방을 생성하는 메서드입니다.")
    @PostMapping("/mogakko")
    public ResponseEntity<Message> createMogakko(@Valid @RequestBody MogakkoRoomCreateRequestDto mogakkoRoomCreateRequestDto,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception {
        return mogakkPostService.createMogakko(mogakkoRoomCreateRequestDto, userDetails.getMember());
    }

    @Operation(summary = "모각코 방 입장 API", description = "모각코 방에 입장하는 메서드입니다.")
    @PostMapping("/mogakko/{sessionId}")
    public ResponseEntity<Message> enterMogakko(@PathVariable(name = "sessionId") String sessionId,
                                                @RequestBody(required = false) MogakkoRoomEnterDataRequestDto requestData,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) throws OpenViduJavaClientException, OpenViduHttpException {
        return mogakkPostService.enterMogakko(sessionId, requestData, userDetails.getMember());
    }

    @Operation(summary = "주변 12km 모각코 목록 조회 / 검색 API", description = "내 위치 기반 12km 이내 모각코 방 전체 목록을 조회하는 메서드입니다.")
    @PostMapping("/mogakkos")
    public ResponseEntity<Message> getAllMogakkos(@RequestParam(required = false) String searchKeyword, @RequestParam(required = false) String language,
                                                  @RequestBody Mogakko12kmRequestDto mogakko12KmRequestDto) {
        return mogakkPostService.getAllMogakkosOrSearch(searchKeyword, language, mogakko12KmRequestDto);
    }
}
