package com.togedog.chat.dto;

import com.togedog.chat.entity.Chat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ChatDto {
    @Getter
    @Setter
    public static class Send{
        @NotNull(message = "메시지 타입은 필수 값입니다.")
        private Chat.MessageType type;

        @NotBlank(message = "내용은 필수 값입니다.")
        private String content;

        @NotBlank(message = "송신자는 필수 값입니다.")
        private String sender;

        private String sendTime;
    }

    @Getter
    @Setter
    public static class Response{
        private Chat.MessageType type;
        private String content;
        private String sender;
        private String sendTime;
    }

//    @Override
//    public String toString() {
//        return "ChatMessage{" +
//                "type=" + type +
//                ", content='" + content + '\'' +
//                ", sender='" + sender + '\'' +
//                '}';
//    }
}