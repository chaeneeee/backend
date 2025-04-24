package com.togedog.friend.entity;

import com.togedog.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "friend", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"requester_email", "recipient_email"})
})
@Getter
@Setter
@NoArgsConstructor
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long friendId;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "requester_email")
    private Member member;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "recipient_email")
    private Member friend;

    public void setMember(Member member) {
        this.member = member;
        if (!member.getFriends().contains(this)) {
            member.addFriend(this);
        }
    }

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime modifiedAt = LocalDateTime.now();

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Status friendStatus = Status.PENDING;

    public enum Status {
        PENDING("대기중"),
        ACCEPTED("요청 수락"),
        REJECTED("요청 거절");

        @Getter
        private String status;

        Status(String status) {
            this.status = status;
        }
    }
}
