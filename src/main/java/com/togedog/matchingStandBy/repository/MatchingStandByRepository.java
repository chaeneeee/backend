package com.togedog.matchingStandBy.repository;

import com.togedog.matchingStandBy.entity.MatchingStandBy;
import com.togedog.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchingStandByRepository extends JpaRepository<MatchingStandBy, Long> {
    Optional<MatchingStandBy> findByHostMemberIdAndGuestMemberId(long hostMemberId, long guestMemberId);
    List<MatchingStandBy> findByStatus(MatchingStandBy.Status status);

    Page<MatchingStandBy> findByGuestMember(Member member, Pageable pageable);
    Page<MatchingStandBy> findByHostMemberId(long hostMemberId, Pageable pageable);

    Optional<MatchingStandBy> findByHostMemberIdAndGuestMemberAndStatus(long hostMemberId, Member guestMember, MatchingStandBy.Status status);
    List<MatchingStandBy> findAllByHostMemberIdAndStatus(long hostMemberId,MatchingStandBy.Status status);
    List<MatchingStandBy> findAllByGuestMemberAndStatus(Member member,MatchingStandBy.Status status);

    @Query("SELECT m FROM MatchingStandBy m " +
            "WHERE (m.hostMemberId IN (:memberIds) OR m.guestMemberId IN (:memberIds)) " +
            "AND m.status = :status")
    List<MatchingStandBy> findByHostOrGuestMemberIdsAndStatus(@Param("memberIds") List<Long> memberIds,
                                                              @Param("status") MatchingStandBy.Status status);

    @Query("SELECT m FROM MatchingStandBy m " +
            "WHERE (m.hostMemberId = :memberId OR m.guestMemberId = :memberId) " +
            "AND m.status = :status")
    List<MatchingStandBy> findByHostOrGuestMemberIdAndStatus(@Param("memberId") Long memberId,
                                                              @Param("status") MatchingStandBy.Status status);
}
