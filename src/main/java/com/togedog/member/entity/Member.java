package com.togedog.member.entity;

import com.togedog.board.entity.Board;
import com.togedog.chatRoom.entity.ChatRoomMember;
import com.togedog.friend.entity.Friend;
import com.togedog.likes.entity.Likes;
import com.togedog.matching.entity.Matching;
import com.togedog.matchingStandBy.entity.MatchingStandBy;
import com.togedog.pet.entity.Pet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "member")
@Getter
@Setter
@NoArgsConstructor
public class Member{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long memberId;

    @Column(name = "member_name", nullable = false)
    private String name;

    @Column(name = "member_nickname", nullable = false)
    private String nickName;

    @Column(name = "member_birth", nullable = false)
    private String birth;

    @Column(name = "member_email", nullable = false)
    private String email;

    @Column(name = "member_password", nullable = false)
    private String password;

    @Column(name = "member_phone", nullable = false)
    private String phone;

    @Column(name = "main_address", nullable = false)
    private String mainAddress;

    @Column(name = "detail_address", nullable = false)
    private String detailAddress;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "member_gender", nullable = false)
    private memberGender gender;

    @Column(name = "member_profile_image")
    private String profileImage;

    @Column(name = "member_report_cnt", nullable = false)
    private int reportCount;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "member_status", nullable = false)
    private memberStatus status = memberStatus.RESTRICTION;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Pet> pets = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Friend> friends = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Friend> members = new ArrayList<>();

    @OneToMany(mappedBy = "hostMember")
    private List<Matching> matchings = new ArrayList<>();

    @OneToMany(mappedBy = "guestMember")
    private List<MatchingStandBy> matchingStandBys = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
    private List<ChatRoomMember> chatRoomMembers = new ArrayList<>();

    public void addMatchingStandBy(MatchingStandBy matchingStandBy) {
        matchingStandBys.add(matchingStandBy);
        if (matchingStandBy.getGuestMember() != this) {
            matchingStandBy.addMember(this);
        }
    }

    public void addPet(Pet pet) {
        pets.add(pet);
        if (pet.getMember() != this) {
            pet.setMember(this);
        }
    }

    public void addMatching(Matching matching) {
        matchings.add(matching);
        if (matching.getHostMember() != this) {
            matching.addMember(this);
        }
    }

    public void addFriend(Friend friend) {
        this.friends.add(friend);
        if (friend.getMember() != this) {
            friend.setMember(this);
        }
    }

    public void addMember(Friend friend) {
        this.members.add(friend);
        if (friend.getMember() != this) {
            friend.setMember(this);
        }
    }

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Likes> likes = new ArrayList<>();

    public void setLike(Likes like) {
        this.likes.add(like);
        if(like.getMember() != this) {
            like.setMember(this);
        }
    }

    @OneToMany(mappedBy = "member")
    private List<Board> boards = new ArrayList<>();

    public void setBoard(Board board) {
        this.boards.add(board);
        if(board.getMember() != this) {
            board.setMember(this);
        }
    }

    public enum memberGender {
        M("남성"),
        F("여성");

        @Getter
        private String gender;

        memberGender(String gender) {
            this.gender = gender;
        }
    }

    public enum memberStatus {
        LOGGED_IN("온라인"),
        LOGGED_OUT("오프라인"),
        RESTRICTION("비활성"),
        DELETED("탈퇴");

        @Getter
        private String status;

        memberStatus(String status) {
            this.status = status;
        }
    }
    // 엑세스 리프리시 토큰. key 쌍으로
    // h2 같은 인메모리방식, nosql

    // 화면줌 마커정보 레디스 범위만큼 캐싱

    public Member(String email) {
        this.email = email;
    }
}
