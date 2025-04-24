package com.togedog.matching.mapper;

import com.togedog.matching.dto.MatchingDto;
import com.togedog.matching.entity.Matching;
import com.togedog.member.entity.Member;
import com.togedog.pet.entity.Pet;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MatchingMapper {
    Matching matchingPostDtoToMatching(MatchingDto.Post post);
    Matching matchingPatchDtoToMatching(MatchingDto.Patch patch);
    default MatchingDto.Response matchingToMatchingResponseDto(Matching matching){
        return new MatchingDto.Response(
                matching.getMatchId(),
                matching.getLatitude(),
                matching.getLongitude(),
                matching.getMatchStatus(),
                matching.getHostMember().getMemberId()
        );
    }

    default MatchingDto.ResponseCard matchingToMatchingResponseCardDto(Matching matching) {
        Member member = matching.getHostMember();
        List<Pet> pets = member.getPets();
        Pet pet = pets.get(0);
        return new MatchingDto.ResponseCard(
                matching.getMatchId(),
                member.getBirth().substring(0, 4),
                member.getGender().getGender(),
                member.getNickName(),
                pet.getPetName(),
                pet.getPetProfileImage(),
                pet.getPetGender(),
                pet.getPetBirth().substring(0,4),
                pet.getPetBreed(),
                pet.getPetSize(),
                pet.getPetPersonality(),
                pet.getPetNeutered()
        );
    }
    List<MatchingDto.Response> matchingToMatchingResponsesDto(List<Matching> matchings);
}
