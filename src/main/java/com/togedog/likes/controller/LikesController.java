package com.togedog.likes.controller;

import com.togedog.likes.dto.LikesDto;
import com.togedog.likes.service.LikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikesController {
    private final LikesService likesService;

    @PostMapping
    public ResponseEntity<Void> toggleLike(@RequestBody @Valid LikesDto.Post requestBody,
                                           Authentication authentication) {
        likesService.toggleLikes(requestBody.getMemberId(), requestBody.getBoardId(), authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
