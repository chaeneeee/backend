package com.togedog.pet.controller;

import com.togedog.pet.entity.Pet;
import com.togedog.pet.mapper.PetMapper;
import com.togedog.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
public class PetController {
    private final PetMapper petMapper;
    private final PetService petService;

    @GetMapping("my-pet")
    public ResponseEntity getPets(Authentication authentication) {
        List<Pet> pets = petService.findPet(authentication);

        return new ResponseEntity<>(petMapper.petsToResponse(pets), HttpStatus.OK);
    }
}
