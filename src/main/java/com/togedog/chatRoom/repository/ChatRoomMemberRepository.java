package com.togedog.chatRoom.repository;

import com.togedog.chatRoom.entity.ChatRoomMember;
import com.togedog.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
//    Optional<ChatRoomMember> findByChatRoom(ChatRoom chatRoom);

//        List<MatchingStandBy> findByHostOrGuestMemberIdAndStatus(@Param("memberId") Long memberId,
//                @Param("status") MatchingStandBy.Status status);
//    @Query("SELECT m FROM chatRoomMember m " +
//            "WHERE m.chatRoom.id = :roomId AND m.member.id = :memberId")
    Optional<ChatRoomMember> findByChatRoom_ChatRoomIdAndMember_MemberId(long roomId, long memberId);
    List<ChatRoomMember> findAllByMember(Member member);
    Optional<ChatRoomMember> findByChatRoom_ChatRoomIdAndMember_MemberIdNot(Long roomId, Long memberId);
}
