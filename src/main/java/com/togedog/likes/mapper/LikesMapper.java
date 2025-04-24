package com.togedog.likes.mapper;

import com.togedog.board.entity.Board;
import com.togedog.likes.dto.LikesDto;
import com.togedog.likes.entity.Likes;
import com.togedog.member.entity.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LikesMapper {
    default Likes likesPostDtoToLikes(LikesDto.Post requestBody){
        Member member = new Member();
        Board board = new Board();
        member.setMemberId(requestBody.getMemberId());
        board.setBoardId(requestBody.getBoardId());
        Likes likes = new Likes();
        likes.setBoard(board);
        likes.setMember(member);
        return likes;
    }
}
