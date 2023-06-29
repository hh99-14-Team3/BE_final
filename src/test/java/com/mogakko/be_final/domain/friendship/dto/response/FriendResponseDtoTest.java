package com.mogakko.be_final.domain.friendship.dto.response;

import com.mogakko.be_final.domain.members.entity.Members;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Friend Response Dto 테스트")
@ExtendWith(MockitoExtension.class)
class FriendResponseDtoTest {
    Members member = new Members();
    FriendResponseDto friendResponseDto = FriendResponseDto.builder().member(member).isSelected(true).build();

    @DisplayName("Friend Response Dto - get Member 테스트")
    @Test
    void getMember() {
        Members member1 = friendResponseDto.getMember();
        assertEquals(member, member1);
    }

    @DisplayName("Friend Response Dto - isSelected 테스트")
    @Test
    void isSelected() {
        boolean isSelected1 = friendResponseDto.isSelected();
        assertTrue(isSelected1);
    }

    @DisplayName("Friend Response Dto - NoArgsConstructor 테스트")
    @Test
    void constructor() {
        FriendResponseDto friendResponseDto1 = new FriendResponseDto();
        assertNotNull(friendResponseDto1);
    }
}