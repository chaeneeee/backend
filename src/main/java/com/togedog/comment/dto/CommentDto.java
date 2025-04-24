package com.togedog.comment.dto;

import com.togedog.reply.dto.ReplyDto;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class CommentDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Post {
        @NotBlank
        private String comment;

        private long boardId;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class Patch{

        @NotBlank
        private String comment;

        private long commentId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class Response {
        private long commentId;
        private String comment;
        private String name;
        private long boardId;
        private List<ReplyDto.Response> replies;
    }
}
