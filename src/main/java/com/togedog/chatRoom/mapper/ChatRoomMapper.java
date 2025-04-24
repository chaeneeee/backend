package com.togedog.chatRoom.mapper;

import com.togedog.chatRoom.dto.ChatRoomDto;
import com.togedog.chatRoom.entity.ChatRoom;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ChatRoomMapper {
    ChatRoomDto.Response chatRoomToChatRoomResponseDto(ChatRoom chatRoom);
    default List<ChatRoomDto.Response> chatRoomsToChatRoomResponseDto(List<ChatRoom> chatRooms){
        return chatRooms.stream()
                .map(chat -> {
                    ChatRoomDto.Response response = new ChatRoomDto.Response();
                    response.setChatRoomMembers(chat.getChatRoomMembers());
                    response.setChatRoomName(chat.getChatRoomName());
                    response.setChatDataPath(chat.getChatDataPath());
                    response.setUpdatedAt(chat.getUpdatedAt());
                    return response;
                })
                .collect(Collectors.toList());
    };
}
