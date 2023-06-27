package com.mogakko.be_final.domain.directMessage.util;

import com.mogakko.be_final.domain.directMessage.entity.DirectMessage;
import com.mogakko.be_final.domain.directMessage.repository.DirectMessageRepository;
import com.mogakko.be_final.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.mogakko.be_final.exception.ErrorCode.MESSAGE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class DirectMessageServiceUtilMethodTest {

    @Mock
    DirectMessageRepository directMessageRepository;
    @InjectMocks
    DirectMessageServiceUtilMethod directMessageServiceUtilMethod;

    DirectMessage directMessage = DirectMessage.builder()
            .id(1L)
            .build();

    @Nested
    @DisplayName("findDirectMessageById Method 테스트")
    class FindDirectMessageById {
        @DisplayName("findDirectMessageById 성공 테스트")
        @Test
        void findDirectMessageById_success() {
            // given
            Long id = 1L;

            when(directMessageRepository.findById(id)).thenReturn(Optional.of(directMessage));

            // when
            DirectMessage foundDirectMessage = directMessageServiceUtilMethod.findDirectMessageById(id);

            // then
            assertEquals(directMessage, foundDirectMessage);
        }

        @DisplayName("findDirectMessageById 실패 테스트")
        @Test
        void findDirectMessageById_fail() {
            // given
            Long id = 1L;

            when(directMessageRepository.findById(id)).thenReturn(Optional.empty());

            // when & then
            CustomException customException = assertThrows(CustomException.class, ()-> directMessageServiceUtilMethod.findDirectMessageById(id));
            assertEquals(MESSAGE_NOT_FOUND, customException.getErrorCode());
        }
    }
}