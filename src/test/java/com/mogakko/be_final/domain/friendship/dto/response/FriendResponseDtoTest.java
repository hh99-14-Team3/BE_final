package com.mogakko.be_final.domain.friendship.dto.response;

import com.mogakko.be_final.domain.members.entity.Members;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("Friend Response Dto 테스트")
@ExtendWith(MockitoExtension.class)
class FriendResponseDtoTest {
    Members member = new Members();
    FriendResponseDto friendResponseDto = FriendResponseDto.builder().member(member).isSelected(true).build();

    @DisplayName("Friend Response Dto - get Member 테스트")
    @Test
    void getMember() {
        Members member1 = friendResponseDto.getMember();
        Assertions.assertEquals(member, member1);
    }

    @DisplayName("Friend Response Dto - isSelected 테스트")
    @Test
    void isSelected() {
        boolean isSelected1 = friendResponseDto.isSelected();
        Assertions.assertTrue(isSelected1);
    }
}