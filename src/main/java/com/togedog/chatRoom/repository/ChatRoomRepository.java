package com.togedog.chatRoom.repository;

import com.togedog.chatRoom.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT m FROM ChatRoom m WHERE m.chatRoomId IN (:chatRoomIds)")
    List<ChatRoom> findAllByCustom(@Param("chatRoomIds") List<Long> chatRoomIds);

}
