package com.togedog.chatRoom.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.togedog.audit.Auditable;
import com.togedog.chat.entity.Chat;
import com.togedog.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class ChatRoom extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long chatRoomId;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "chat_room_name")
    private String chatRoomName;

    @Column(name = "chat_data_path")
    private String chatDataPath;

    @Column(name = "UUID")
    private final String UUID = java.util.UUID.randomUUID().toString();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private List<Chat> chats = new ArrayList<>();
    public void addChat(Chat chat) {
        chats.add(chat);
        if (chat.getChatRoom() != this) {
            chat.addChatRoom(this);
        }
    }

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private List<ChatRoomMember> chatRoomMembers = new ArrayList<>();

    public void addMember(Member member) {
        if (chatRoomMembers.size() >= 2) {
            throw new IllegalStateException("한 채팅방에는 최대 2명만 추가 가능합니다.");
        }
        ChatRoomMember chatRoomMember = new ChatRoomMember(this, member);
        chatRoomMembers.add(chatRoomMember);
        member.getChatRoomMembers().add(chatRoomMember);
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
