package com.togedog.pet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class PetDto {
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Post {
        private String petProfileImage;

        @NotNull(message = "반려견의 이름을 입력해주세요.")
        private String petName;

        @NotNull(message = "반려견의 성격을 간단히 작성해주세요.")
        @Size(max = 20, message = "반려견의 성격은 최대 20자까지 작성할 수 있습니다.")
        private String petPersonality;

        @NotNull(message = "반려견의 품종을 입력해주세요.")
        private String petBreed;

        @NotNull(message = "반려견의 출생일을 입력해주세요.")
        private String petBirth;

        @Pattern(regexp = "Y|N", message = "중성화 여부를 'Y' 과 'N'로 입력해 주세요.")
        private String petNeutered;

        @Pattern(regexp = "M|F", message = "성별을 'M' 과 'F'로 입력해 주세요.")
        private String petGender;

        @Pattern(regexp = "S|M|L", message = "반려견의 크기를 'S', 'M' ,'L' 으로 입력해 주세요.")
        private String petSize;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response {
        private String petProfileImage;

        private String petName;

        private String petPersonality;

        private String petBreed;

        private String petBirth;

        private String petNeutered;

        private String petGender;

        private String petSize;
    }
}
