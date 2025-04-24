package com.togedog.matchingStandBy.entity;

import com.togedog.audit.Auditable;
import com.togedog.matching.entity.Matching;
import com.togedog.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class MatchingStandBy extends Auditable  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long matchingStandById;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private Status status = Status.STATUS_WAIT;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(5);

    @Column(name = "hostMemberId")
    private long hostMemberId;

    @Column(name = "guestMemberId")
    private long guestMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member guestMember;

    public void addMember(Member member) {
        if (!member.getMatchingStandBys().contains(this)) {
            member.addMatchingStandBy(this);
        }
        this.guestMember = member;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MATCHING_ID")
    private Matching matching;
    public void addMatching(Matching matching) {
        if (!matching.getMatchingStandBys().contains(this)) {
            matching.addMatchingStandBy(this);
        }
        this.matching = matching;
    }


    @AllArgsConstructor
    public enum Status{
        STATUS_WAIT(1,"응답 대기"),
        STATUS_FAIL(2,"거부 상태"),
        STATUS_SUCCESS(3,"매칭 성공");

        @Getter
        private int statusNumber;

        @Getter
        private String statusDescription;
    }



}
