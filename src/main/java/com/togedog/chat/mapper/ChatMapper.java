package com.togedog.chat.mapper;

import com.togedog.chat.dto.ChatDto;
import com.togedog.chat.entity.Chat;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    Chat chatSendDtoToChat(ChatDto.Send chat);

    default ChatDto.Response chatToChatResponseDto(Chat chat){
        ChatDto.Response response = new ChatDto.Response();
        response.setContent(chat.getContent());
        response.setType(chat.getType());
        response.setSender(chat.getSender());
        response.setSendTime(chat.getSendTime());
        return response;
    }

    default List<ChatDto.Response> chatToChatResponseDto(List<Chat> chatList){
        return chatList.stream()
                .map(chat -> {
                    ChatDto.Response response = new ChatDto.Response();
                    response.setContent(chat.getContent());
                    response.setType(chat.getType());
                    response.setSender(chat.getSender());
                    response.setSendTime(chat.getSendTime());
                    return response;
                })
                .collect(Collectors.toList());
    }
}