package com.togedog.pet.mapper;

import com.togedog.pet.dto.PetDto;
import com.togedog.pet.entity.Pet;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PetMapper {
    List<PetDto.Response> petsToResponse(List<Pet> pets);
}
