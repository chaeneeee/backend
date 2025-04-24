package com.togedog.chat.controller;

import com.togedog.chat.dto.ChatDto;
import com.togedog.chat.entity.Chat;
import com.togedog.chat.mapper.ChatMapper;
import com.togedog.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate template;
    private final ChatService chatService;
    private final ChatMapper chatMapper;

    @MessageMapping("/chat/{chatroom-id}")
    public void sendMessage(@Payload @Valid ChatDto.Send chatDto, @DestinationVariable("chatroom-id") Long chatRoomId) {
//        ChatDto.Response response = new ChatDto.Response();
        Chat response = chatService.MessageSendAndSave(chatMapper.chatSendDtoToChat(chatDto),chatRoomId);
        ChatDto.Response result = chatMapper.chatToChatResponseDto(response);
        template.convertAndSend("/sub/chat/" + chatRoomId, result);
    }
}