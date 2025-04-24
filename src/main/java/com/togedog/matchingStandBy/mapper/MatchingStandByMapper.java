package com.togedog.matchingStandBy.mapper;

import com.togedog.matchingStandBy.dto.MatchingStandByDto;
import com.togedog.matchingStandBy.entity.MatchingStandBy;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MatchingStandByMapper {
    MatchingStandBy matchingStandByPatchDtoToMatchingStandBy(MatchingStandByDto.Patch patch);

    @Named("toResponseHost")
    default List<MatchingStandByDto.Responses> matchingStandBysToMatchingStandByDtoResponseHost(List<MatchingStandBy> matchingStandBys){
         return matchingStandBys
                 .stream()
                 .map(matchingStandBy -> MatchingStandByDto.Responses
                         .builder()
                         .matchingStandById(matchingStandBy.getMatchingStandById())
                         .status(matchingStandBy.getStatus().getStatusNumber() == 1 ? "수락 버튼" :
                                 matchingStandBy.getStatus().getStatusDescription())
                         .partnerNickName(matchingStandBy.getGuestMember().getNickName())
                         .partnerPetImage(matchingStandBy.getGuestMember().getProfileImage())
                         .createdAt(matchingStandBy.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")))
                         .build())
                 .collect(Collectors.toList());
    }

    @Named("toResponseGuest")
    default List<MatchingStandByDto.Responses> matchingStandBysToMatchingStandByDtoResponseGuest(List<MatchingStandBy> matchingStandBys) {
        return matchingStandBys
                .stream()
                .map(matchingStandBy -> MatchingStandByDto.Responses
                        .builder()
                        .matchingStandById(matchingStandBy.getMatchingStandById())
                        .status(matchingStandBy.getStatus().getStatusDescription())
                        .partnerNickName(matchingStandBy.getMatching().getHostMember().getNickName())
                        .partnerPetImage(matchingStandBy.getMatching().getHostMember().getProfileImage())
                        .createdAt(matchingStandBy.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")))
                        .build())
                .collect(Collectors.toList());
    };
}
