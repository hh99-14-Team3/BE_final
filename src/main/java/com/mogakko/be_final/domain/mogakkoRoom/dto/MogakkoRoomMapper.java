package com.mogakko.be_final.domain.mogakkoRoom.dto;

import com.mogakko.be_final.domain.mogakkoRoom.dto.response.MogakkoRoomResponseDto;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoom;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring") //스프링에서 사용하기 위해 인터페이스 위에 설정
public interface MogakkoRoomMapper {

    List<MogakkoRoomResponseDto> roomsToRoomResponseDtos(List<MogakkoRoom> rooms);

}
