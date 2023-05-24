package com.mogakko.be_final.domain.chatroom.dto;

import com.mogakko.be_final.domain.chatroom.dto.response.ChatRoomResponseDto;
import com.mogakko.be_final.domain.chatroom.entity.ChatRoom;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring") //스프링에서 사용하기 위해 인터페이스 위에 설정
public interface ChatRoomMapper {

    List<ChatRoomResponseDto> roomsToRoomResponseDtos(List<ChatRoom> rooms);

}
