package com.togedog.auth.controller;

import com.togedog.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/logout")
    public ResponseEntity postLogout(Authentication authentication) {
        String username = authentication.getName();
        return authService.logout(username) ?
                new ResponseEntity(HttpStatus.OK) : new ResponseEntity(HttpStatus.FORBIDDEN);
    }
}
