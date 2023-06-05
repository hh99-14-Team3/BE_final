package com.mogakko.be_final.domain.mogakkoRoom.controller;

import com.mogakko.be_final.domain.mogakkoRoom.dto.request.Mogakko12kmRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.dto.request.MogakkoRoomCreateRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.dto.request.MogakkoRoomEnterDataRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.dto.request.MogakkoTimerRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.service.MogakkoService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "모각코방 관련 API", description = "모각코방 관련 API 입니다.")
public class MogakkoController {

    private final MogakkoService mogakkoService;

    @PostMapping("/mogakko")
    @Operation(summary = "모각코 방 생성 API", description = "모각코 방을 생성하는 메서드입니다.")
    public ResponseEntity<Message> createMogakko(@Valid @RequestBody MogakkoRoomCreateRequestDto mogakkoRoomCreateRequestDto,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception {
        return mogakkoService.createMogakko(mogakkoRoomCreateRequestDto, userDetails.getMember());
    }

    @PostMapping("/mogakko/{sessionId}")
    @Operation(summary = "모각코 방 입장 API", description = "모각코 방에 입장하는 메서드입니다.")
    public ResponseEntity<Message> enterMogakko(@PathVariable(name = "sessionId") String sessionId,
                                                @RequestBody(required = false) MogakkoRoomEnterDataRequestDto requestData,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) throws OpenViduJavaClientException, OpenViduHttpException {
        return mogakkoService.enterMogakko(sessionId, requestData, userDetails.getMember());
    }

    @DeleteMapping("/mogakko/{sessionId}")
    @Operation(summary = "모각코 방 퇴장 API", description = "퇴장하기 버튼 눌렀을때 모각코 방이 퇴장되는 메서드입니다.")
    public ResponseEntity<Message> outClickMogakko(@PathVariable(name = "sessionId") String sessionId,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mogakkoService.outMogakko(sessionId, userDetails.getMember());
    }

    @PostMapping("/mogakkos")
    @Operation(summary = "주변 12km 모각코 목록 조회 / 검색 API", description = "내 위치 기반 12km 이내 모각코 방 전체 목록을 조회하는 메서드입니다.")
    public ResponseEntity<Message> getAllMogakkos(@RequestParam(required = false) String searchKeyword, @RequestParam(required = false) String language,
                                                  @RequestBody Mogakko12kmRequestDto mogakko12KmRequestDto) {
        return mogakkoService.getAllMogakkosOrSearch(searchKeyword, language, mogakko12KmRequestDto);
    }

    @GetMapping("/mogakkos/top")
    @Operation(summary = "인기 지역 모각코 조회 API", description = "인기있는 지역의 모각코를 조회하는 메서드입니다.")
    public ResponseEntity<Message> topMogakko() {
        return mogakkoService.topMogakko();
    }


    @GetMapping("/mogakko/{sessionId}/members")
    @Operation(summary = "모각코 유저 조회 API", description = "모각코에 있는 유저의 정보를 조회하는 메서드입니다.")
    public ResponseEntity<Message> getMogakkoMembersData(@PathVariable(name = "sessionId") String sessionId,
                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mogakkoService.getMogakkoMembersData(sessionId, userDetails.getMember());
    }

    @PostMapping("/mogakko/timer")
    @Operation(summary = "모각코 타이머 API", description = "모각코 시간을 측정하는 메서드입니다.")
    public ResponseEntity<Message> mogakkoTimer(@RequestBody MogakkoTimerRequestDto mogakkoTimerRequestDto,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails){
        return mogakkoService.mogakkoTimer(mogakkoTimerRequestDto, userDetails.getMember());
    }
}
