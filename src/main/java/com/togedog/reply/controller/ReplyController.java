package com.togedog.reply.controller;

import com.togedog.auth.service.AuthService;
import com.togedog.reply.dto.ReplyDto;
import com.togedog.reply.entity.Reply;
import com.togedog.reply.mapper.ReplyMapper;
import com.togedog.reply.service.ReplyService;
import com.togedog.utils.UriCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;
    private final ReplyMapper replyMapper;
    private final AuthService authService;
    private final static String REPLY_DEF_URL = "/replies";

    @PostMapping("/comments/{comment-id}/replies")
    public ResponseEntity<Void> postReply(@PathVariable("comment-id") @Positive long commentId,
                                          @Valid @RequestBody ReplyDto.Post requestBody,
                                          Authentication authentication) {
        requestBody.setCommentId(commentId);
        Reply reply = replyService.createReply(replyMapper.replyPostToReply(requestBody), authentication);
        URI location = UriCreator.createUri(REPLY_DEF_URL, reply.getReplyId());
        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/replies/{reply-id}")
    public ResponseEntity patchReply(@PathVariable("reply-id") @Positive long replyId,
                                     @Valid @RequestBody ReplyDto.Patch requestBody,
                                     Authentication authentication) {
        String email = null;
        if (authentication != null) {
            email = (String) authentication.getPrincipal();
            boolean isLoggedOut = !authService.isTokenValid(email);
            if (isLoggedOut) {
                return new ResponseEntity<>("User Logged Out", HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        requestBody.setReplyId(replyId);
        Reply reply = replyService.updateReply(replyMapper.replyPatchToReply(requestBody), authentication);
        return new ResponseEntity<>(replyMapper.replyToReplyResponse(reply), HttpStatus.OK);
    }

    @DeleteMapping("/replies/{reply-id}")
    public ResponseEntity deleteReply(@PathVariable("reply-id") @Positive long replyId,
                                      Authentication authentication) {
        String email = null;
        if (authentication != null) {
            email = (String) authentication.getPrincipal();
            boolean isLoggedOut = !authService.isTokenValid(email);
            if (isLoggedOut) {
                return new ResponseEntity<>("User Logged Out", HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        replyService.deleteReply(replyId,authentication);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
