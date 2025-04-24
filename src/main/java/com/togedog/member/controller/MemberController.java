package com.togedog.member.controller;

import com.togedog.auth.service.AuthService;
import com.togedog.dto.MultiResponseDto;
import com.togedog.dto.SingleResponseDto;
import com.togedog.email.service.EmailVerificationService;
import com.togedog.friend.service.FriendService;
import com.togedog.member.dto.MemberDto;
import com.togedog.member.dto.VerificationRequest;
import com.togedog.member.entity.Member;
import com.togedog.member.mapper.MemberMapper;
import com.togedog.member.service.MemberService;
import com.togedog.utils.UriCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;


@RestController
@Validated
@RequestMapping
@RequiredArgsConstructor
public class MemberController {
    private final static String MEMBER_DEFAULT_URL = "/members";
    private final MemberService service;
    private final FriendService friendService;
    private final MemberMapper memberMapper;
    private final EmailVerificationService emailVerificationService;
    private final AuthService authService;

    // 이메일 인증코드 전송
    @PostMapping("/auth-code")
    public ResponseEntity signUpMember(@Valid @RequestBody VerificationRequest verificationRequest) {
        emailVerificationService.sendCodeToEmail(verificationRequest.getEmail());

        return ResponseEntity.accepted().body("이메일로 인증 코드를 전송했습니다. 인증 코드를 입력하여 회원가입을 완료하세요.");
    }

    // 인증코드 검증
    @PostMapping("/verify-auth-code")
    public ResponseEntity verifyEmail(@Valid @RequestBody VerificationRequest verificationRequest) {
        String email = verificationRequest.getEmail();
        String authCode = verificationRequest.getAuthCode();

        boolean isVerified = emailVerificationService.verifyCode(email, authCode);
        if (!isVerified) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 코드가 올바르지 않습니다.");
        }
        return ResponseEntity.ok("이메일 인증이 완료되었습니다. 회원가입을 진행하세요.");
    }

    // 회원가입 완료
    @PostMapping("/members")
    public ResponseEntity completeSignUp(@Valid @RequestBody MemberDto.Post memberDto) {
        Member member = memberMapper.memberPostToMember(memberDto);
        service.createMember(member);
        URI location = UriCreator.createUri(MEMBER_DEFAULT_URL, member.getMemberId());

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/member")
    public ResponseEntity getMember(Authentication authentication) {
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
        Member member = service.findMember(authentication);

        return new ResponseEntity<>(
                new SingleResponseDto<>(memberMapper.memberToResponseDto(member)), HttpStatus.OK);
    }

    @GetMapping("/members")
    public ResponseEntity getMembers(@RequestParam @Positive int page,
                                     @RequestParam @Positive int size,
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
        Page<Member> pageMembers = service.findMembers(page -1, size, authentication);
        List<Member> members = pageMembers.getContent();

        return new ResponseEntity(
                new MultiResponseDto<>(memberMapper.membersToResponseDto(members), pageMembers), HttpStatus.OK);
    }

    @PatchMapping("/members")
    public ResponseEntity patchMember(Authentication authentication,
                                      @Valid @RequestBody MemberDto.Patch patch) {
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
        Member member = service.updateMember(memberMapper.memberPatchToMember(patch),authentication);

        return new ResponseEntity<>(
                new SingleResponseDto<>(memberMapper.memberToResponseDto(member)), HttpStatus.OK);
    }

    @DeleteMapping("/members")
    public ResponseEntity deleteMember(Authentication authentication) {
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
        service.deleteMember(authentication);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/friends/{friend-email}")
    public ResponseEntity addFriend(@PathVariable("friend-email") String toEmail,
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
        String fromEmail = authentication.getName();
        friendService.addFriend(fromEmail, toEmail);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/accept/{friend-id}")
    public ResponseEntity acceptFriendRequest(@PathVariable("friend-id") @Positive long friendId,
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
        friendService.acceptFriendRequest(friendId, authentication);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/reject/{friend-id}")
    public ResponseEntity rejectFriendRequest(@PathVariable("friend-id") @Positive long friendId,
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
        friendService.rejectFriendRequest(friendId, authentication);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/find-my-id")
    public ResponseEntity findMyId(@RequestBody MemberDto.findId dto) {
        Member email = service.findMemberId(memberMapper.memberFindIdToMember(dto));
        return new ResponseEntity<>(memberMapper.memberToResponseId(email), HttpStatus.OK);
    }
}
