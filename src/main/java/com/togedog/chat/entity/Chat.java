package com.togedog.chat.entity;

import com.togedog.chatRoom.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long chatId;

    @Column(name = "message_type",nullable = false)
    @Enumerated(value = EnumType.STRING)
    private MessageType type = MessageType.CHAT;

    @Column(name = "content",nullable = false)
    private String content;

    @Column(name = "sender",nullable = false)
    private String sender;

    @Column(name = "date",nullable = false)
    private String sendTime;

    @ManyToOne//(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "CHATROOM_ID")
    private ChatRoom chatRoom;

    public void addChatRoom(ChatRoom chatRoom) {
        if (!chatRoom.getChats().contains(this)) {
            chatRoom.addChat(this);
        }
        this.chatRoom = chatRoom;
    }

    public enum MessageType {
        CHAT, JOIN, LEAVE, ERROR,
    }
}