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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.togedog.eventListener.EventCaseEnum.EventCase.DELETE_MARKER;
import static com.togedog.eventListener.EventCaseEnum.EventCase.DELETE_RELATED_MATCHING_STAND_BY_DATA;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchingService {
    private final MatchingRepository matchingRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;
    public Matching createMatch(Matching matching, Authentication authentication) {
        Member member = extractMemberFromAuthentication(authentication);

        Optional<Matching> optionalMatching =
                matchingRepository.findByHostMemberAndMatchStatus(member, Matching.MatchStatus.MATCH_HOSTING);


        if (optionalMatching.isPresent()) {
            Matching findMatch = optionalMatching.get();
            findMatch.setLatitude(matching.getLatitude());
            findMatch.setLongitude(matching.getLongitude());
            return matchingRepository.save(findMatch);
        } else {
            // ✅ 새로운 매칭 생성
            matching.setHostMember(member);
            matching.setHostMemberId(member.getMemberId());
            Matching saved = matchingRepository.save(matching);
            System.out.println("📌 매칭 저장 완료: matchId = " + saved.getMatchId());
            return saved;
        }
    }


    public Matching updateMatch(Matching matching, Authentication authentication) {
        Member member = extractMemberFromAuthentication(authentication);
        Matching findMatching = matchingRepository.findByHostMemberAndMatchStatus(member, Matching.MatchStatus.MATCH_HOSTING)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MATCH_NOT_FOUND));

        Optional.ofNullable(matching.getMatchStatus()).ifPresent(findMatching::setMatchStatus);

        if (matching.getMatchStatus() == Matching.MatchStatus.MATCH_CANCEL) {
            CustomEvent event = new CustomEvent(this, DELETE_RELATED_MATCHING_STAND_BY_DATA, findMatching.getHostMember().getMemberId());
            eventPublisher.publishEvent(event);
            event = new CustomEvent(this, DELETE_MARKER, findMatching.getHostMember().getEmail());
            eventPublisher.publishEvent(event);

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

        List<Matching> updatedMatchings = findMatchings.stream()
                .filter(m -> m.getMatchStatus() != Matching.MatchStatus.MATCH_SUCCESS)
                .peek(m -> m.setMatchStatus(Matching.MatchStatus.MATCH_SUCCESS))

                .collect(Collectors.toList());

        if (!updatedMatchings.isEmpty()) {
            matchingRepository.saveAll(updatedMatchings);
        }
    }

    public Page<Matching> findMatches(int page, int size) {
        return matchingRepository.findAll(PageRequest.of(page, size, Sort.by("matchId").descending()));
    }

    @Transactional(readOnly = true)
    public Matching findVerifiedMatch(String email, Authentication authentication) {
        extractMemberFromAuthentication(authentication);
        Matching result = matchingRepository.findByHostMember_EmailAndMatchStatus(email, Matching.MatchStatus.MATCH_HOSTING)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MATCH_NOT_FOUND));
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        result.setHostMember(member);
        return result;
    }

    @Transactional(readOnly = true)
    public Matching findVerifiedMatch(Authentication authentication) {
        Member member = extractMemberFromAuthentication(authentication);
        return matchingRepository.findByHostMember(member)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MATCH_NOT_FOUND));
    }

    private Member extractMemberFromAuthentication(Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }
}
