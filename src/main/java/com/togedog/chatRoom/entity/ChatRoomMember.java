package com.togedog.chatRoom.entity;

import com.togedog.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class ChatRoomMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long chatRoomMemberId;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    @Enumerated(value = EnumType.STRING)
    private ViewStatus status = ViewStatus.VIEW_OFF;

    @Column
    private int notReadCount = 0;

    public ChatRoomMember(ChatRoom chatRoom, Member member) {
        this.chatRoom = chatRoom;
        this.member = member;
    }

    @AllArgsConstructor
    public enum ViewStatus{
        VIEW_ON(1,"보는중"),
        VIEW_OFF(2,"안보는중");

        @Getter
        private int statusNumber;

        @Getter
        private String statusDescription;
    }
}
