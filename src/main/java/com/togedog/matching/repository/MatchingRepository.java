package com.togedog.matching.repository;

import com.togedog.matching.entity.Matching;
import com.togedog.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchingRepository extends JpaRepository<Matching, Long>{
    Optional<Matching> findByHostMemberAndMatchStatus(Member member, Matching.MatchStatus matchStatus);
    Optional<Matching> findByHostMember_EmailAndMatchStatus(String string, Matching.MatchStatus matchStatus);
    Optional<Matching> findByHostMember(Member member);

    List<Matching> findByHostMemberIdOrHostMemberId(long hostMemberId, long guestMemberId);

        @Query("SELECT m FROM Matching m WHERE ((m.hostMember.memberId = :memberId AND m.guestMember.memberId = :guestId) " +
                "OR (m.hostMember.memberId = :guestId AND m.guestMember.memberId = :memberId)) " +
                "AND m.matchStatus IN ('MATCH_REQUESTED', 'MATCH_CONFIRMED')") // 완료된 매칭은 제외
        Optional<Matching> findOngoingMatchBetweenMembers(@Param("memberId") Long memberId, @Param("guestId") Long guestId);




}