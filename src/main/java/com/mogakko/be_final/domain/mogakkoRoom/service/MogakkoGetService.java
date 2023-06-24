package com.mogakko.be_final.domain.mogakkoRoom.service;

import com.mogakko.be_final.domain.mogakkoRoom.dto.response.NeighborhoodResponseDto;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomRepository;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MogakkoGetService {

    private final MogakkoRoomRepository mogakkoRoomRepository;

    // 인기 지역 모각코 조회
    @Transactional(readOnly = true)
    public ResponseEntity<Message> topMogakko() {
        List<NeighborhoodResponseDto> mogakkoRoomList = mogakkoRoomRepository.findTop4NeighborhoodsOrderByCountDesc();
        if (mogakkoRoomList.size() == 0) return new ResponseEntity<>(new Message("조회된 지역이 없습니다.", null), HttpStatus.OK);
        if (mogakkoRoomList.size() >= 4) mogakkoRoomList = mogakkoRoomList.subList(0, 4);
        return new ResponseEntity<>(new Message("인기 지역 모각코 조회 성공", mogakkoRoomList), HttpStatus.OK);
    }
}
