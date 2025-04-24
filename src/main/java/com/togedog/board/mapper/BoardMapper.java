package com.togedog.board.mapper;

import com.togedog.board.dto.BoardDto;
import com.togedog.board.dto.BoardDto.ResponseBoard;
import com.togedog.board.entity.*;
import com.togedog.comment.dto.CommentDto.Response;
import com.togedog.comment.entity.Comment;
import com.togedog.member.entity.Member;
import com.togedog.reply.dto.ReplyDto;
import com.togedog.reply.entity.Reply;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface BoardMapper {
    default Board boardDtoPostToBoard(BoardDto.Post requestBody){
        Member member = new Member();
        Board board;
        if(requestBody.getBoardType() == BoardType.REVIEW) {
            board = new BoardReview();
        } else if (requestBody.getBoardType() == BoardType.BOAST) {
            board = new BoardBoast();
        } else if (requestBody.getBoardType() == BoardType.INQUIRY) {
            board = new BoardInquiry();
        }else if (requestBody.getBoardType() == BoardType.ANNOUNCEMENT){
            board = new BoardAnnouncement();
        }else {
            board = new Board();
        }
        board.setTitle(requestBody.getTitle());
        board.setContent(requestBody.getContent());
        board.setContentImg(requestBody.getContentImg());
        board.setBoardType(requestBody.getBoardType());
        board.setMember(member);
        return board;
    }

    Board boardDtoPatchToBoard(BoardDto.Patch requestBody);

    default ResponseBoard boardToBoardDtoResponse(Board board) {
        ResponseBoard responseBoard = new ResponseBoard();

        responseBoard.setBoardId(board.getBoardId());
        responseBoard.setTitle(board.getTitle());
        responseBoard.setAuthor(board.getMember().getName());
        responseBoard.setContent(board.getContent());
        responseBoard.setContentImg(board.getContentImg());
        responseBoard.setBoardType(board.getBoardType().getBoardDescription());
        responseBoard.setBoardStatus(board.getBoardStatus().getStatusDescription());
        responseBoard.setLikesCount(board.getLikesCount() != null ? board.getLikesCount() : 0);
        responseBoard.setViewCount(board.getViewCount() != null ? board.getViewCount() : 0);

        List<Response> commentList = board.getComments()
                .stream()
                .filter(c -> c.getCommentStatus() != Comment.CommentStatus.COMMENT_DELETED)
                .map(c -> {
                    Response commentResponseDto = new Response();
                    commentResponseDto.setCommentId(c.getCommentId());
                    commentResponseDto.setComment(c.getComment());
                    commentResponseDto.setName(c.getMember().getName());
                    commentResponseDto.setBoardId(c.getBoard().getBoardId());

                    List<ReplyDto.Response> replyList = c.getReplies().stream()
                            .filter(r -> r.getReplyStatus() != Reply.ReplyStatus.REPLY_DELETED)
                            .map(r -> ReplyDto.Response.builder()
                                    .replyId(r.getReplyId())
                                    .reply(r.getReply())
                                    .name(r.getMember().getName())
                                    .build())
                            .collect(Collectors.toList());

                    commentResponseDto.setReplies(replyList);
                    return commentResponseDto;
                })
                .collect(Collectors.toList());

        // 댓글 리스트를 responseBoard에 설정
        responseBoard.setComments(commentList);

        return responseBoard;
    }

    default List<BoardDto.Response> boardToBoardDtoResponses(List<Board> boards) {
        return boards
                .stream()
                .filter(status -> !status.getBoardStatus().equals(Board.BoardStatus.BOARD_DELETED))
                .map(board -> BoardDto.Response
                        .builder()
                        .boardId(board.getBoardId())
                        .title(board.getTitle())
                        .author(board.getMember().getName())
                        .content(board.getContent())
                        .contentImg(board.getContentImg())
                        .boardType(board.getBoardType().getBoardDescription())
                        .boardStatus(board.getBoardStatus().getStatusDescription())
                        .likesCount(board.getLikesCount() != null ? board.getLikesCount() : 0)
                        .viewCount(board.getViewCount() != null ? board.getViewCount() : 0)
                        .build())
                .collect(Collectors.toList());
    }
}