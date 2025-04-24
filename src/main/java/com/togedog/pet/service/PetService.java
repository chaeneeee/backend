package com.togedog.pet.service;

import com.togedog.exception.BusinessLogicException;
import com.togedog.exception.ExceptionCode;
import com.togedog.member.entity.Member;
import com.togedog.member.repository.MemberRepository;
import com.togedog.pet.entity.Pet;
import com.togedog.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService {
    private final PetRepository petRepository;
    private final MemberRepository memberRepository;

    public List<Pet> findPet(Authentication authentication) {
        String email = authentication.getName();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        List<Pet> pets = petRepository.findByMember(member);

        if (pets.isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.PET_NOT_FOUND);
        }
        return pets;
    }
}
