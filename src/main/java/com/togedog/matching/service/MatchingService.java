package com.togedog.matching.service;

import com.togedog.eventListener.CustomEvent;
import com.togedog.exception.BusinessLogicException;
import com.togedog.exception.ExceptionCode;
import com.togedog.matching.entity.Matching;
import com.togedog.matching.repository.MatchingRepository;
import com.togedog.member.entity.Member;
import com.togedog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.togedog.eventListener.EventCaseEnum.EventCase.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchingService {
    private final MatchingRepository matchingRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;
    public Matching createMatch(Matching matching, Authentication authentication) {
        Member member = extractMemberFromAuthentication(authentication);

        // 동일한 사용자 간 진행 중인 매칭이 있는지 확인
        Optional<Matching> existingMatch = matchingRepository.findOngoingMatchBetweenMembers(
                member.getMemberId(), matching.getGuestMember().getMemberId()
        );

        if (existingMatch.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.MATCH_ALREADY_EXISTS);
        }

        // 기존에 호스트로 매칭을 보유한 경우 위치만 업데이트
        Optional<Matching> optionalMatching = matchingRepository.findByHostMemberAndMatchStatus(member, Matching.MatchStatus.MATCH_HOSTING);

        if (optionalMatching.isPresent()) {
            Matching findMatch = optionalMatching.get();
            findMatch.setLatitude(matching.getLatitude());
            findMatch.setLongitude(matching.getLongitude());
            return matchingRepository.save(findMatch);
        } else {
            // ✅ 새로운 매칭 생성
            matching.setHostMember(member);
            matching.setHostMemberId(member.getMemberId());
            return matchingRepository.save(matching);
        }
    }


    public Matching updateMatch(Matching matching, Authentication authentication) {
        Member member = extractMemberFromAuthentication(authentication);
        Matching findMatching = matchingRepository.findByHostMemberAndMatchStatus(member, Matching.MatchStatus.MATCH_HOSTING)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MATCH_NOT_FOUND));

        Optional.ofNullable(matching.getMatchStatus()).ifPresent(findMatching::setMatchStatus);

        if (matching.getMatchStatus() == Matching.MatchStatus.MATCH_CANCEL) {
            publishMatchCancelEvents(findMatching);
        }

        return matchingRepository.save(findMatching);
    }

    private void publishMatchCancelEvents(Matching findMatching) {
        CustomEvent deleteStandbyEvent = new CustomEvent(this, DELETE_RELATED_MATCHING_STAND_BY_DATA,
                findMatching.getHostMember().getMemberId());
        eventPublisher.publishEvent(deleteStandbyEvent);

        CustomEvent deleteMarkerEvent = new CustomEvent(this, DELETE_MARKER,
                findMatching.getHostMember().getEmail());
        eventPublisher.publishEvent(deleteMarkerEvent);
    }


    public void updateMatchForCustomEvent(Long hostMemberId, Long guestMemberId) {
        List<Matching> findMatchings = matchingRepository.findByHostMemberIdOrHostMemberId(hostMemberId, guestMemberId);

        // 상태가 변경된 경우에만 저장
        List<Matching> updatedMatchings = findMatchings.stream()
                .filter(matching -> matching.getMatchStatus() != Matching.MatchStatus.MATCH_SUCCESS)  // 기존 상태 확인
                .peek(matching -> matching.setMatchStatus(Matching.MatchStatus.MATCH_SUCCESS))         // 상태 변경
                .collect(Collectors.toList());

        if (!updatedMatchings.isEmpty()) {
            matchingRepository.saveAll(updatedMatchings);
        }
    }

    public Page<Matching> findMatches(int page, int size) {
        return matchingRepository.findAll(PageRequest.of(page, size, Sort.by("matchId").descending()));
    }

    @Transactional(readOnly = true)
    public Matching findVerifiedMatch(String email,Authentication authentication) {
        extractMemberFromAuthentication(authentication);
        Optional<Matching> findMatch =
                matchingRepository.findByHostMember_EmailAndMatchStatus(email, Matching.MatchStatus.MATCH_HOSTING);
        Matching result = findMatch.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MATCH_NOT_FOUND));
        Member member = memberRepository.findByEmail(email).orElseThrow(()->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        result.setHostMember(member);

        return result;
    }
    @Transactional(readOnly = true)
    public Matching findVerifiedMatch(Authentication authentication) {
        Member member = extractMemberFromAuthentication(authentication);
        Optional<Matching> findMatch =
                matchingRepository.findByHostMember(member);
        Matching result = findMatch.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MATCH_NOT_FOUND));
        return result;
    }

//    private void findCheckOtherMatchStatusHosting(Member member) {
//        Optional<Matching> findMatch = matchingRepository.findByHostMemberAndMatchStatus(member, Matching.MatchStatus.MATCH_HOSTING);
//        findMatch.ifPresent(match -> {
//            throw new BusinessLogicException(ExceptionCode.MATCH_ALREADY_EXISTS);
//        });
//    }

    private Member extractMemberFromAuthentication(Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }
//    private void changeMatchStandByStatusWaitToReject(long memberId) {
//        List<MatchStandBy> MatchStandBys = matchStandByRepository.findByMatchIdAndStatus(memberId, MatchStandBy.Status.MATCHSTANDBY_WAIT);
//
//        for (MatchStandBy matchStandBy : matchStandBys) {
//            if (matchStandBy.getStatus().equals(MatchStandBy.Status.MATCHSTANDBY_WAIT)) {
//                matchStandBy.setStatus(MatchStandBy.Status.MATCHSTANDBY_REJECT);
//                //이후 알람 보내야함
//            }
//        }
//    }
}
