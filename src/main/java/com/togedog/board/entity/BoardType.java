package com.togedog.board.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@AllArgsConstructor
public enum BoardType {
    INQUIRY("신고&문의 게시판"),
    REVIEW("후기 게시판"),
    BOAST("자랑 게시판"),
    ANNOUNCEMENT("공지 게시판");

    @Getter
    private String boardDescription;
}
