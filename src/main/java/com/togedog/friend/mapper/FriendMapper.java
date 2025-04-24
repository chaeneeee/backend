package com.togedog.friend.mapper;

import com.togedog.friend.dto.Dto;
import com.togedog.friend.entity.Friend;
import com.togedog.member.entity.Member;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;
//
//        "friendEmail": null,
//                "friendName": null,
//                "friendNickName": null,
//                "friendPhone": null
@Mapper(componentModel = "spring")
public interface FriendMapper {
    Dto.Response friendToResponse(Friend friend);

    default List<Dto.Response> friendsToResponse(List<Member> friends) {
        List<Dto.Response> result = new ArrayList<>();
        for (Member member : friends) {
            Dto.Response response = new Dto.Response();
            response.setFriendEmail(member.getEmail());
            response.setFriendName(member.getName());
            response.setFriendNickName(member.getNickName());
            response.setFriendPhone(member.getPhone());
            response.setFriendBirth(member.getBirth());
            result.add(response);
        }
        return result;
    }

    default List<Dto.Response> friendsToResponses(List<Friend> friends) {
        List<Dto.Response> receipientEmailList = new ArrayList<>();
        for (Friend friend : friends) {
            Member member = friend.getMember();
            Dto.Response response = new Dto.Response();
            response.setId(friend.getFriendId()); //friendId
            response.setFriendEmail(member.getEmail());
            response.setFriendName(member.getName());
            response.setFriendNickName(member.getNickName());
            response.setFriendPhone(member.getPhone());
            receipientEmailList.add(response);
        }
        return receipientEmailList;
    }
}
