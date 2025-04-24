package com.togedog.reply.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

public class ReplyDto {
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Post{
        @NotBlank
        private String reply;

        private long commentId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Patch{

        @NotBlank
        private String reply;

        private long replyId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private long replyId;
        private String reply;
        private String name;
    }
}
