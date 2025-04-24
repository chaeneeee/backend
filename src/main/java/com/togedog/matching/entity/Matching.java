package com.togedog.matching.entity;

import com.togedog.audit.Auditable;
import com.togedog.matchingStandBy.entity.MatchingStandBy;
import com.togedog.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = "hostMember", callSuper = false)
public class Matching extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long matchId;

    @Column(name = "latitude",nullable = false)
    private double latitude;

    @Column(name = "longitude",nullable = false)
    private double longitude;

    @Column(name = "matching_status")
    @Enumerated(value = EnumType.STRING)
    private MatchStatus matchStatus = MatchStatus.MATCH_HOSTING;

    @Column(name = "host_member_id")
    private long hostMemberId;

    @OneToMany(mappedBy = "matching")
    private List<MatchingStandBy> matchingStandBys = new ArrayList<>();
    public void addMatchingStandBy(MatchingStandBy matchingStandBy) {
        matchingStandBys.add(matchingStandBy);
        if (matchingStandBy.getMatching() != this) {
            matchingStandBy.addMatching(this);
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member hostMember;

    public void addMember(Member member) {
        if (!member.getMatchings().contains(this)) {
            member.addMatching(this);
        }
        this.hostMember = member;
    }
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_member_id")
    private Member guestMember; // 새로운 매칭 상대방 필드 추가

    @AllArgsConstructor
    public enum MatchStatus{
        MATCH_HOSTING(1,"매칭 호스팅 중"),
        MATCH_CANCEL(2,"매칭 취소"),
        MATCH_SUCCESS(3,"매칭 성공"),
        MATCH_COMPLETED(4, "매칭 완료");

        @Getter
        private int statusNumber;

        @Getter
        private String statusDescription;
    }
}
