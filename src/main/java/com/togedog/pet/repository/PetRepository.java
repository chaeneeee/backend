package com.togedog.pet.repository;

import com.togedog.member.entity.Member;
import com.togedog.pet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByMember(Member member);
}
