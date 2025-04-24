package com.togedog.chatRoom.service;

import com.togedog.chat.entity.Chat;
import com.togedog.chat.repository.ChatRepository;
import com.togedog.chatRoom.entity.ChatRoom;
import com.togedog.chatRoom.entity.ChatRoomMember;
import com.togedog.chatRoom.repository.ChatRoomMemberRepository;
import com.togedog.chatRoom.repository.ChatRoomRepository;
import com.togedog.exception.BusinessLogicException;
import com.togedog.exception.ExceptionCode;
import com.togedog.member.entity.Member;
import com.togedog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatRepository chatRepository;

    public void createChatRoom(List<Member> members) {
        ChatRoom chatRoom = new ChatRoom();
        if(members.size()!=2)
            throw new BusinessLogicException(ExceptionCode.MATCH_NOT_FOUND);
        Member member1 = members.get(0);
        Member member2 = members.get(1);
        String chatRoomName = member1.getName() + "-" + member2.getName();
        chatRoom.addMember(member1);
        chatRoom.addMember(member2);
        chatRoom.setChatRoomName(chatRoomName);
        chatRoomRepository.save(chatRoom);
    }

    public List<Chat> findVerifiedChatRoomInChat(long chatRoomId, Authentication authentication) {
        Member member = extractMemberFromAuthentication(authentication);
        chatRoomMemberRepository.
                findByChatRoom_ChatRoomIdAndMember_MemberId(chatRoomId,member.getMemberId()).
                orElseThrow(()-> new BusinessLogicException(ExceptionCode.CHATROOM_NOT_FOUND));
        return chatRepository.findAllByChatRoom_ChatRoomId(chatRoomId);
    }
    public List<ChatRoom> findVerifiedChatRoom(Authentication authentication) {
        Member member = extractMemberFromAuthentication(authentication);
        List<ChatRoomMember> chatRoomMembers = chatRoomMemberRepository.findAllByMember(member);
        List<Long> chatRoomIds = new ArrayList<>();
        for (ChatRoomMember chatRoomMember : chatRoomMembers) {
            chatRoomIds.add(chatRoomMember.getChatRoom().getChatRoomId());
        }
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByCustom(chatRoomIds);
        for(ChatRoom chatRoom : chatRoomList){
            chatRoom.setChatRoomName(makeChatRoomName(chatRoom.getChatRoomName(),member.getNickName()));
        }
        return chatRoomList;
    }

    private String makeChatRoomName(String string, String memberName) {
        string = string.replace("-","");
        string = string.replace(memberName,"");
        return string;
    }

    private Member extractMemberFromAuthentication(Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

}
