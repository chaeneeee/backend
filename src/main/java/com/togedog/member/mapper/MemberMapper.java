package com.togedog.member.mapper;

import com.togedog.member.dto.MemberDto;
import com.togedog.member.entity.Member;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    @Mapping(target = "pets", source = "pets")
    Member memberPostToMember(MemberDto.Post post);

    @AfterMapping
    default void afterMapping(@MappingTarget Member member) {
        if(member.getPets() != null) {
            member.getPets().forEach(pet -> pet.setMember(member));
        }
    }

    Member memberPatchToMember(MemberDto.Patch patch);

    MemberDto.Response memberToResponseDto(Member member);

    MemberDto.responseEmail memberToResponseId(Member member);

    List<MemberDto.Response> membersToResponseDto(List<Member> members);

    Member memberFindIdToMember(MemberDto.findId dto);
}
