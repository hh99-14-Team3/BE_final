package com.mogakko.be_final.domain.mogakkoRoom.service;

import com.mogakko.be_final.domain.mogakkoRoom.dto.response.NeighborhoodResponseDto;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomRepository;
import com.mogakko.be_final.util.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MogakkoGetServiceTest {

    @Mock
    MogakkoRoomRepository mogakkoRoomRepository;
    @InjectMocks
    MogakkoGetService mogakkoGetService;


    @Nested
    @DisplayName("인기 모각코 지역 조회 테스트")
    class TopMogakko {
        @DisplayName("인기 모각코 지역 조회 성공 테스트")
        @Test
        void topMogakko_success() {
            // given
            List<NeighborhoodResponseDto> mogakkoList = new ArrayList<>();
            NeighborhoodResponseDto neighborhoodResponseDto1 = NeighborhoodResponseDto.builder()
                    .count(7)
                    .neighborhood("가남읍")
                    .build();
            NeighborhoodResponseDto neighborhoodResponseDto2 = NeighborhoodResponseDto.builder()
                    .count(5)
                    .neighborhood("성수동")
                    .build();
            NeighborhoodResponseDto neighborhoodResponseDto3 = NeighborhoodResponseDto.builder()
                    .count(4)
                    .neighborhood("부발읍")
                    .build();
            NeighborhoodResponseDto neighborhoodResponseDto4 = NeighborhoodResponseDto.builder()
                    .count(3)
                    .neighborhood("???")
                    .build();
            NeighborhoodResponseDto neighborhoodResponseDto5 = NeighborhoodResponseDto.builder()
                    .count(1)
                    .neighborhood("동네임")
                    .build();
            mogakkoList.add(neighborhoodResponseDto1);
            mogakkoList.add(neighborhoodResponseDto2);
            mogakkoList.add(neighborhoodResponseDto3);
            mogakkoList.add(neighborhoodResponseDto4);
            mogakkoList.add(neighborhoodResponseDto5);

            when(mogakkoRoomRepository.findTop4NeighborhoodsOrderByCountDesc()).thenReturn(mogakkoList);

            // when
            ResponseEntity<Message> response = mogakkoGetService.topMogakko();

            // then
            List<NeighborhoodResponseDto> responseDtoList = (List<NeighborhoodResponseDto>) response.getBody().getData();
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody().getMessage(), "인기 지역 모각코 조회 성공");
            assertEquals(responseDtoList.size(), 4);
            for (int i = 0; i < responseDtoList.size(); i++) {
                assertEquals(responseDtoList.get(i).getNeighborhood(), mogakkoList.get(i).getNeighborhood());
                assertEquals(responseDtoList.get(i).getCount(), mogakkoList.get(i).getCount());
            }
        }

        @DisplayName("인기 모각코 지역 조회 결과 4개 이하 테스트")
        @Test
        void topMogakko_successUnderFour() {
            // given
            List<NeighborhoodResponseDto> mogakkoList = new ArrayList<>();
            NeighborhoodResponseDto neighborhoodResponseDto1 = NeighborhoodResponseDto.builder()
                    .count(7)
                    .neighborhood("가남읍")
                    .build();
            NeighborhoodResponseDto neighborhoodResponseDto2 = NeighborhoodResponseDto.builder()
                    .count(5)
                    .neighborhood("성수동")
                    .build();
            NeighborhoodResponseDto neighborhoodResponseDto3 = NeighborhoodResponseDto.builder()
                    .count(4)
                    .neighborhood("부발읍")
                    .build();

            mogakkoList.add(neighborhoodResponseDto1);
            mogakkoList.add(neighborhoodResponseDto2);
            mogakkoList.add(neighborhoodResponseDto3);

            when(mogakkoRoomRepository.findTop4NeighborhoodsOrderByCountDesc()).thenReturn(mogakkoList);

            // when
            ResponseEntity<Message> response = mogakkoGetService.topMogakko();

            // then
            List<NeighborhoodResponseDto> responseDtoList = (List<NeighborhoodResponseDto>) response.getBody().getData();
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody().getMessage(), "인기 지역 모각코 조회 성공");
            assertEquals(responseDtoList.size(), 3);
            for (int i = 0; i < responseDtoList.size(); i++) {
                assertEquals(responseDtoList.get(i).getNeighborhood(), mogakkoList.get(i).getNeighborhood());
                assertEquals(responseDtoList.get(i).getCount(), mogakkoList.get(i).getCount());
            }
        }

        @DisplayName("인기 모각코 지역 조회 결과 없음 테스트")
        @Test
        void topMogakko_noNeighborhood() {
            // given
            List<NeighborhoodResponseDto> mogakkoList = new ArrayList<>();

            when(mogakkoRoomRepository.findTop4NeighborhoodsOrderByCountDesc()).thenReturn(mogakkoList);

            // when
            ResponseEntity<Message> response = mogakkoGetService.topMogakko();

            // then
            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertEquals(response.getBody().getMessage(), "조회된 지역이 없습니다.");
        }
    }
}