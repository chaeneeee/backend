package com.togedog.board.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.togedog.audit.Auditable;
import com.togedog.comment.entity.Comment;
import com.togedog.likes.entity.Likes;
import com.togedog.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@DiscriminatorColumn
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Board extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long boardId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String contentImg;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToMany(mappedBy = "board")
    private List<Likes> likes = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Comment> comments = new ArrayList<>();

    @Column
    private Integer likesCount = 0;

    @Column
    private Integer viewCount = 0;

    @Column
    @Enumerated(value = EnumType.STRING)
    private BoardStatus boardStatus = BoardStatus.BOARD_POST;


    @AllArgsConstructor
    public enum BoardStatus{
        BOARD_POST("게시글 게시 상태"),
        BOARD_DELETED("게시글 삭제 상태");

        @Getter
        @Setter
        private String statusDescription;
    }

    @Column
    @Enumerated(value = EnumType.STRING)
    private BoardType boardType;

    public void incrementViewCount() {
        this.viewCount++;
    }
}
