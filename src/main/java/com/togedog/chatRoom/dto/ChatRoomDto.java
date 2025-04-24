package com.togedog.chatRoom.dto;

import com.togedog.chatRoom.entity.ChatRoomMember;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class ChatRoomDto {
    @Setter
    @Getter
    public static class Response {
        private String chatRoomName;
        private String chatDataPath;
        private LocalDateTime updatedAt;
        private List<ChatRoomMember> chatRoomMembers;
    }
}
