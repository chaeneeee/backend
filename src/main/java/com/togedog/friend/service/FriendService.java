package com.togedog.friend.service;

import com.togedog.exception.BusinessLogicException;
import com.togedog.exception.ExceptionCode;
import com.togedog.friend.entity.Friend;
import com.togedog.friend.repository.FriendRepository;
import com.togedog.member.entity.Member;
import com.togedog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendService {
    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;

    public Friend getFriend(String memberEmail, String friendEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        Member friend = memberRepository.findByEmail(friendEmail)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        return friendRepository.findByMemberAndFriend(member, friend)
                .orElseThrow(()-> new IllegalArgumentException("친구요청을 찾을수 없음"));
    }

    public List<Friend> getFriendRequests(String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        List<Friend> friendRequests = friendRepository.
                findByFriendAndFriendStatusNot(member,Friend.Status.ACCEPTED);
        return friendRequests;
//        return friendRequests.stream()
//                .filter(friend -> !friend.getFriendStatus().equals(Friend.Status.ACCEPTED))
//                .map(Friend::getMember)
//                .collect(Collectors.toList());
    }

    @Transactional
    public Page<Friend> getFriends(int page, int size) {
        return friendRepository.findAll(PageRequest.of(page, size,
                Sort.by("memberId").descending()));
    }

    @Transactional(readOnly = true)
    public List<Member> getsFriends(String email, Friend.Status status) {
        Member byEmail = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        List<Friend> friends = friendRepository.findByMemberAndFriendStatus(byEmail, status);
        List<Member> members = new ArrayList<>();
        for (Friend friend : friends) {
            members.add(friend.getFriend());
        }

        return members;
    }

//    public List<Member> getFriendsOfUser(Authentication authentication) {
//        String email = authentication.getName(); // 현재 로그인한 사용자의 이메일
//        Member currentUser = memberRepository.findByEmail(email)
//                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
//
//        // 친구 목록 조회
//        List<Friend> friendships = friendRepository.findAllByMember(currentUser);
//
//        // 친구 목록에서 친구(Member) 객체만 추출하여 리스트로 반환
//        return friendships.stream()
//                .map(Fr::getFriend)
//                .collect(Collectors.toList());
//    }

    @Transactional
    public void addFriend(String fromEmail, String toEmail) {
        Member member = memberRepository.findByEmail(fromEmail)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        Member friendMember = memberRepository.findByEmail(toEmail)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        Friend friend = new Friend();
        friend.setFriend(friendMember);
        friend.setMember(member);
        friend.setFriendStatus(Friend.Status.PENDING);
        friendRepository.save(friend);
    }

    @Transactional
    public void acceptFriendRequest(long friendId, Authentication authentication) {
        Member recipient = extractMemberFromAuthentication(authentication);

        Optional<Friend> optionalFriend = friendRepository.findById(friendId);

        Friend friend = optionalFriend.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.FRIEND_REQUEST_NOT_FOUND));

        friend.setFriendStatus(Friend.Status.ACCEPTED);

        friendRepository.save(friend);

        // verifedMember.getFriend -> 팬딩상태를 수락으로 변경 후 저장
        // 다음으로 verifedMember.요청이던 수락이던 맴버 둘 다 찾아서, 서로 위치가 바뀐 friend 객체 새롭게 만들고
        // 해당 friend의 상태를 수락으로 바꾸고 저장
        createReverseFriendship(friend);
    }

    private void createReverseFriendship(Friend friend) {
        // 역방향 친구 관계 생성
        Friend reverseFriend = new Friend();
        reverseFriend.setMember(friend.getFriend()); // 요청을 받은 사람이 역으로 요청을 보낸 사람을 친구로 설정
        reverseFriend.setFriend(friend.getMember());
        reverseFriend.setFriendStatus(Friend.Status.ACCEPTED);
        friendRepository.save(reverseFriend);
    }

//    @Transactional
//    public void rejectFriendRequest(String forEmail, String toEmail) {
//        Member member = memberRepository.findByEmail(forEmail)
//                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
//        Member friendMember = memberRepository.findByEmail(toEmail)
//                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
//
//        Friend friendRequest = friendRepository.findByMemberAndFriend(member, friendMember)
//                .orElseThrow(()-> new IllegalArgumentException("친구요청을 찾을수 없음"));
//
//        friendRepository.delete(friendRequest);
//    }

    @Transactional
    public void rejectFriendRequest(long friendId, Authentication authentication) {
        Member recipient = extractMemberFromAuthentication(authentication);

        Optional<Friend> optionalFriend = friendRepository.findById(friendId);

        Friend friend = optionalFriend.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.FRIEND_REQUEST_NOT_FOUND));

        // 친구 요청을 거절하면, 요청을 삭제합니다.
        friendRepository.delete(friend);
    }

    private Member extractMemberFromAuthentication(Authentication authentication) {
        /**
         * 첫 번째 if 블록에서는 메서드로 전달된 authentication 객체가 null인 경우,
         * SecurityContextHolder에서 인증 정보를 가져오려고 시도.
         * 두 번째 if 블록에서는 authentication이 여전히 null인 경우,
         * 사용자에게 인증되지 않았음을 알리고, 처리할 수 있도록 예외를 발생시킴.
         */
//        if (authentication == null) {
//            authentication = SecurityContextHolder.getContext().getAuthentication();
//        }
//
//        if (authentication == null) {
//            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
//        }

        String username = (String) authentication.getPrincipal();
        return memberRepository.findByEmail(username)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

}
