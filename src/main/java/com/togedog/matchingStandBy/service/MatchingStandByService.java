package com.togedog.matchingStandBy.service;

import com.togedog.eventListener.CustomEvent;
import com.togedog.exception.BusinessLogicException;
import com.togedog.exception.ExceptionCode;
import com.togedog.matching.entity.Matching;
import com.togedog.matching.repository.MatchingRepository;
import com.togedog.matchingStandBy.entity.MatchingStandBy;
import com.togedog.matchingStandBy.repository.MatchingStandByRepository;
import com.togedog.member.entity.Member;
import com.togedog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.togedog.eventListener.EventCaseEnum.EventCase.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchingStandByService {
    private final MatchingStandByRepository repository;
    private final MemberRepository memberRepository;
    private final MatchingRepository matchingRepository;
    private final ApplicationEventPublisher eventPublisher;

    private final ReentrantLock lock = new ReentrantLock();
    
    public MatchingStandBy createMatchingStandBy(Authentication authentication,
                                                 long matchingId) {
        lock.lock();
        try {
            MatchingStandBy matchingStandBy = new MatchingStandBy();
            Member member = extractMemberFromAuthentication(authentication);
            Matching matching = matchingRepository.findById(matchingId).orElseThrow(() ->
                    new BusinessLogicException(ExceptionCode.MATCH_NOT_FOUND));
            long hostMemberId = matching.getHostMember().getMemberId();
            long guestMemberId = member.getMemberId();
            if(hostMemberId == member.getMemberId())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong Request");
            repository.findByHostMemberIdAndGuestMemberId(hostMemberId,guestMemberId).ifPresent(result -> {
                throw new BusinessLogicException(ExceptionCode.MATCH_ALREADY_EXISTS);
            });
            repository.findByHostMemberIdAndGuestMemberId(guestMemberId,hostMemberId).ifPresent(result -> {
                throw new BusinessLogicException(ExceptionCode.REQUEST_ALREADY_RECEIVED_FROM_OTHER_PARTY);
            });
            matchingStandBy.setHostMemberId(hostMemberId);
            matchingStandBy.setGuestMemberId(guestMemberId);
            matchingStandBy.setMatching(matching);
            matchingStandBy.setGuestMember(member);
            return repository.save(matchingStandBy);
        }finally {
            lock.unlock();
        }
    }
    
    public MatchingStandBy updateMatchingStandBy(MatchingStandBy matchingStandBy,Authentication authentication) {
        lock.lock();
        try {
            Member member = extractMemberFromAuthentication(authentication);
            //param으로 입력받은 id가 실제로 테이블에 존재하는지?
            MatchingStandBy findMatchingStandBy = repository.findById(matchingStandBy.getMatchingStandById()).orElseThrow(() ->
                    new BusinessLogicException(ExceptionCode.NOT_MATCH_HOST_MEMBER));

            //api 보낸 이가 해당 데이터에 HostMember 이거나 GuestMember 인지?
            if (findMatchingStandBy.getHostMemberId() == member.getMemberId() || findMatchingStandBy.getGuestMember().equals(member)) {
                Optional.ofNullable(matchingStandBy.getStatus())
                        .ifPresent(findMatchingStandBy::setStatus);
                //HostMember 가 수락했다는 Patch 이면..
                if(matchingStandBy.getStatus().equals(MatchingStandBy.Status.STATUS_SUCCESS)) {
                    List<Long> memberIds = new ArrayList<>();
                    memberIds.add(findMatchingStandBy.getHostMemberId());
                    memberIds.add(findMatchingStandBy.getGuestMemberId());
                    CustomEvent event = new CustomEvent(this, SUCCESS_RELATED_MATCHING_DATA, memberIds);
                    eventPublisher.publishEvent(event);
                    event = new CustomEvent(this, CREATE_CHAT_ROOM, memberIds);
                    eventPublisher.publishEvent(event);
                    extractedDataAndChangeStatusToFail(memberIds,MatchingStandBy.Status.STATUS_WAIT);
                    event = new CustomEvent(this, DELETE_MARKER,
                            findMatchingStandBy.getGuestMember().getEmail());
                    eventPublisher.publishEvent(event);
                    event = new CustomEvent(this, DELETE_MARKER,
                            findMatchingStandBy.getMatching().getHostMember().getEmail());
                    eventPublisher.publishEvent(event);
                }
                return repository.save(findMatchingStandBy);
            } else {
                throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
            }
        }finally {
            lock.unlock();
        }
    }

    private void extractedDataAndChangeStatusToFail(List<Long> memberIds, MatchingStandBy.Status status) {
        List<MatchingStandBy> findMSBList =
                repository.findByHostOrGuestMemberIdsAndStatus(memberIds,status);
        List<MatchingStandBy> updatedMSBList = findMSBList.stream()
                .peek(MSB -> MSB.setStatus(MatchingStandBy.Status.STATUS_FAIL)) // 상태 변경
                .collect(Collectors.toList());
        repository.saveAll(updatedMSBList);
    }
    public void extractedDataAndChangeStatusToFail(Long memberId, MatchingStandBy.Status status) {
        List<MatchingStandBy> findMSBList =
                repository.findByHostOrGuestMemberIdAndStatus(memberId,status);
        List<MatchingStandBy> updatedMSBList = findMSBList.stream()
                .peek(MSB -> MSB.setStatus(MatchingStandBy.Status.STATUS_FAIL)) // 상태 변경
                .collect(Collectors.toList());
        repository.saveAll(updatedMSBList);
    }

    public void changeStatusToTimeOut(LocalDateTime nowLocalDateTime) {
        lock.lock();
        try {
            List<MatchingStandBy> updatedList = repository.findByStatus(MatchingStandBy.Status.STATUS_WAIT).stream()
                    .filter(matchingStandBy -> Duration.between(nowLocalDateTime, matchingStandBy.getCreatedAt()).toMinutes() <= -5)
                    .peek(matchingStandBy -> matchingStandBy.setStatus(MatchingStandBy.Status.STATUS_FAIL))
                    .collect(Collectors.toList());
            if (!updatedList.isEmpty())
                repository.saveAll(updatedList);
        } finally {
            lock.unlock();
        }
    }

    public List<MatchingStandBy> findHostMatchingStandBys(Authentication authentication) {
        Member hostMember = extractMemberFromAuthentication(authentication);
        return repository.findAllByHostMemberIdAndStatus(hostMember.getMemberId(), MatchingStandBy.Status.STATUS_WAIT);
    }
    public List<MatchingStandBy> findGuestMatchingStandBys(Authentication authentication) {
        Member guestMember = extractMemberFromAuthentication(authentication);
        return repository.findAllByGuestMemberAndStatus(guestMember, MatchingStandBy.Status.STATUS_WAIT);
    }
    public boolean findMatchAlreadyExists(long matchingId, Authentication authentication) {
        Member guestMember = extractMemberFromAuthentication(authentication);
        Matching matching = matchingRepository.findById(matchingId).orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MATCH_NOT_FOUND));
        return repository.findByHostMemberIdAndGuestMemberAndStatus(
                matching.getHostMemberId(),
                guestMember,
                MatchingStandBy.Status.STATUS_WAIT
        ).isPresent();
    }

    private Member extractMemberFromAuthentication(Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }
}
