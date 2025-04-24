package com.togedog.chatRoom.controller;

import com.togedog.chat.entity.Chat;
import com.togedog.chat.mapper.ChatMapper;
import com.togedog.chatRoom.entity.ChatRoom;
import com.togedog.chatRoom.mapper.ChatRoomMapper;
import com.togedog.chatRoom.service.ChatRoomService;
import com.togedog.dto.SingleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chatroom")
@Validated
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final ChatMapper chatMapper;
    private final ChatRoomMapper chatRoomMapper;

    /**
    * 해당 채팅방의 채팅 내역 전체 출력
    *
    * @param 채팅방 Id, 사용자 인증 정보
    * @return 채팅내역
    * @author Tizesin(신민준)
    */
    @GetMapping("/chat/{chatroom-id}")
    public ResponseEntity getChatFromChatRoom(@PathVariable("chatroom-id") long chatRoomId,
                                      Authentication authentication) {
        List<Chat> chatList = chatRoomService.findVerifiedChatRoomInChat(chatRoomId, authentication);

        return new ResponseEntity<>(
                new SingleResponseDto<>(chatMapper.chatToChatResponseDto(chatList)), HttpStatus.OK);
    }

    /**
    * 등록이 되어있는 채팅방 리스트 조회
    *
    * @param 사용자 인증 정보
    * @return 채팅방 리스트
    * @author Tizesin(신민준)
    */
    @GetMapping
    public ResponseEntity getChatRoomList(Authentication authentication) {
        List<ChatRoom> chatList = chatRoomService.findVerifiedChatRoom(authentication);
        return new ResponseEntity<>(
                new SingleResponseDto<>(chatRoomMapper.chatRoomsToChatRoomResponseDto(chatList)), HttpStatus.OK);
    }
}
