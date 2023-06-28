package com.mogakko.be_final.domain.mogakkoRoom.controller;

import com.mogakko.be_final.domain.mogakkoRoom.dto.response.NeighborhoodResponseDto;
import com.mogakko.be_final.domain.mogakkoRoom.service.MogakkoGetService;
import com.mogakko.be_final.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Mogakko Controller - [GET] 테스트")
class MogakkoGetControllerTest {
    @Mock
    private MogakkoGetService mogakkoGetService;
    @InjectMocks
    private MogakkoGetController mogakkoGetController;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(mogakkoGetController).build();
    }

    @DisplayName("[GET] 인기 지역 모각코 조회 테스트")
    @Test
    void topMogakko() throws Exception {
        List<NeighborhoodResponseDto> mogakkoRoomList = new ArrayList<>();
        Message expectedMessage = new Message("인기 지역 모각코 조회 성공", mogakkoRoomList);

        when(mogakkoGetService.topMogakko()).thenReturn(ResponseEntity.ok(expectedMessage));

        mockMvc.perform(MockMvcRequestBuilders.get("/mogakkos/top"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage.getMessage()));

        verify(mogakkoGetService, times(1)).topMogakko();
    }
}