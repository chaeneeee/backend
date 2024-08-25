package com.togedog.chat.repository;

import com.togedog.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findAllByChatRoom_ChatRoomId(long chatRoomId);
}
