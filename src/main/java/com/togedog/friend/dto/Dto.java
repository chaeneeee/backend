package com.togedog.friend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class Dto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response {
        private long id;
        private String friendEmail;
        private String friendName;
        private String friendNickName;
        private String friendPhone;
        private String friendBirth;
    }
}
