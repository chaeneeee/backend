package com.togedog.friend.controller;

import com.togedog.auth.service.AuthService;
import com.togedog.friend.dto.Dto;
import com.togedog.friend.entity.Friend;
import com.togedog.friend.mapper.FriendMapper;
import com.togedog.friend.service.FriendService;
import com.togedog.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;
    private final FriendMapper friendMapper;
    private final AuthService authService;

//    @GetMapping("/{friend-email}/{member-email}")
//    public ResponseEntity getFriend(@PathVariable("friend-email") String friendEmail,
//                                    @PathVariable("member-email") String memberEmail) {
//        Friend friend = friendService.getFriend(friendEmail, memberEmail);
//        Dto.Response response = friendMapper.friendToResponse(friend);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

    @GetMapping("/{member-email}")
    public ResponseEntity getFriendRequests(@PathVariable("member-email") String memberEmail) {
        List<Friend> friendRequests = friendService.getFriendRequests(memberEmail);
        List<Dto.Response> responses = friendMapper.friendsToResponses(friendRequests);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }


//    @GetMapping
//    public ResponseEntity getFriends(@RequestParam @Positive int page,
//                                     @RequestParam @Positive int size,
//                                     Authentication authentication) {
//        String email = null;
//        if (authentication != null) {
//            email = (String) authentication.getPrincipal();
//            boolean isLoggedOut = !authService.isTokenValid(email);
//            if (isLoggedOut) {
//                return new ResponseEntity<>("User Logged Out", HttpStatus.UNAUTHORIZED);
//            }
//        } else {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//        Page<Friend> friendPage = friendService.getFriends(page -1, size);
//        List<Friend> friendList = friendPage.getContent();
//
//        return new ResponseEntity<>(new MultiResponseDto<>(
//                    friendMapper.friendsToResponse(friendList), friendPage), HttpStatus.OK);
//    }

    @GetMapping
    public ResponseEntity getsFriends(Authentication authentication) {
        String email;
        if (authentication != null) {
            email = (String) authentication.getPrincipal();
            boolean isLoggedOut = !authService.isTokenValid(email);
            if (isLoggedOut) {
                return new ResponseEntity<>("User Logged Out", HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<Member> friendList = friendService.getsFriends(email, Friend.Status.ACCEPTED);
        List<Dto.Response> responseDtos = friendMapper.friendsToResponse(friendList);

        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

}
