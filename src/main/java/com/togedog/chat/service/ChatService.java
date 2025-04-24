package com.togedog.chat.service;

import com.togedog.chat.entity.Chat;
import com.togedog.chat.mapper.ChatMapper;
import com.togedog.chat.repository.ChatRepository;
import com.togedog.chatRoom.repository.ChatRoomMemberRepository;
import com.togedog.chatRoom.repository.ChatRoomRepository;
import com.togedog.exception.BusinessLogicException;
import com.togedog.exception.ExceptionCode;
import com.togedog.member.entity.Member;
import com.togedog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;
    private final ChatMapper mapper;
    private static final Queue<Chat> chatQueue = new LinkedList<>();
    private static final int chatQueueSize = 10;

    public Chat MessageSendAndSave(Chat chat, Long chatRoomId) {
        Member member = memberRepository.findByEmail(chat.getSender())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        if (chatRoomMemberRepository.findByChatRoom_ChatRoomIdAndMember_MemberId(
                chatRoomId, member.getMemberId()).isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
        }
        chat.setSender(member.getNickName());
        chat.setSendTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        chat.setChatRoom(chatRoomRepository.findById(chatRoomId).orElseThrow(()->
                new BusinessLogicException(ExceptionCode.CHATROOM_NOT_FOUND)));



        saveMessage(chat,"NoQueue");
        return chat;
    }
    public void saveMessage(Chat chat,String queue) {
        if (queue.equals("NoQueue")) {
            chatRepository.save(chat);
        } else {
            chatQueue.add(chat);
            if(chatQueue.size() == chatQueueSize) {// commitChatQueue();
                chatRepository.saveAll(chatQueue);
            }
        }
//        chatRoomMemberRepository.findByChatRoom_ChatRoomIdAnd
    }
    public void saveMessage() {
        if(!chatQueue.isEmpty())
            chatRepository.saveAll(chatQueue);
    }
}
