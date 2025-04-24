package com.togedog.matchingStandBy.dto;

import com.togedog.matchingStandBy.entity.MatchingStandBy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

public class MatchingStandByDto {
    @Setter
    @Getter
    @AllArgsConstructor
    public static class Post{

    }
    @Getter
    @Setter
    public static class Patch {
        private long matchingStandById;

        @NotNull
        private MatchingStandBy.Status status;

    }

    @Getter
    @Setter
    @Builder
    public static class Responses {
        private long matchingStandById;
        private String status;
        private String partnerNickName;
        private String partnerPetImage;
        private String createdAt;
    }
}
