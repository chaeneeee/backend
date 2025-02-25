package com.togedog.matching.repository;

import com.togedog.matching.entity.Matching;
import com.togedog.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface MatchingRepository extends JpaRepository<Matching, Long> {

    // 중복 매칭 방지를 위한 진행 중인 매칭 조회 (MATCH_COMPLETED 제외)
    //서로 매칭을 요청한 경우 찾아 완료된 매칭을 제외하고 매칭 시켜주기
    @Query("SELECT m FROM Matching m WHERE " +
            "((m.hostMember.memberId = :memberId AND m.guestMember.memberId = :guestId) " +
            "OR (m.hostMember.memberId = :guestId AND m.guestMember.memberId = :memberId)) " +
            "AND m.matchStatus IN ('MATCH_REQUESTED', 'MATCH_CONFIRMED')")
    Optional<Matching> findOngoingMatchBetweenMembers(
            @Param("memberId") Long memberId,
            @Param("guestId") Long guestId
    );

    Optional<Matching> findByHostMemberAndMatchStatus(Member member, Matching.MatchStatus matchStatus);
    Optional<Matching> findByHostMember_EmailAndMatchStatus(String email, Matching.MatchStatus matchStatus);
    Optional<Matching> findByHostMember(Member member);

    List<Matching> findByHostMemberIdOrHostMemberId(long hostMemberId, long guestMemberId);
}