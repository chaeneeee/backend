package com.togedog.reply.entity;

import com.togedog.audit.Auditable;
import com.togedog.comment.entity.Comment;
import com.togedog.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reply extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long replyId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String reply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMMENT_ID")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Column
    @Enumerated(value = EnumType.STRING)
    private ReplyStatus replyStatus = ReplyStatus.REPLY_POST;

    @AllArgsConstructor
    public enum ReplyStatus{
        REPLY_POST("댓글 활성 상태"),
        REPLY_DELETED("댓글 삭제 상태");

        @Getter
        @Setter
        private String statusDescription;
    }
}
