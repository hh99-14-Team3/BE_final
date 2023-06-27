package com.mogakko.be_final.domain.friendship.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteFriendRequestDto {
    private List<String> receiverNickname;
}
