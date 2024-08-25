package com.togedog.likes.dto;

import lombok.*;

public class LikesDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Post{

        private long memberId;
        private long boardId;

        public Post(long memberId, long boardId) {
            this.memberId = memberId;
            this.boardId = boardId;
        }
    }
}
