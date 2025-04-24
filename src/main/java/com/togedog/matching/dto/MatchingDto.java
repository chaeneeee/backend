package com.togedog.matching.dto;

import com.togedog.matching.entity.Matching;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class MatchingDto {
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Post{

        private double latitude;

        private double longitude;
    }

    @Getter
    @Setter
    public static class Patch{
        private long matchingId;

//        //jwt 적용 시 삭제
//        @NotBlank
//        private Member hostMember;

        private Matching.MatchStatus matchStatus;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Response {
        private long matchingId;
        private double latitude;
        private double longitude;
        private Matching.MatchStatus matchStatus;
        private long hostMemberId;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @Builder
    public static class ResponseCard {
        private long matchingId;
        private String memberYearOfBirth;
        private String memberGender;
        private String memberNickname;
        private String petName;
        private String petProfile;
        private String petGender;
        private String petYearOfBirth;
        private String petBreed;
        private String petSize;
        private String petPersonality;
        private String petNeutered;

    }
}
