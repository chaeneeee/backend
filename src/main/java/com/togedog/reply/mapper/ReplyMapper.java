package com.togedog.reply.mapper;

import com.togedog.comment.entity.Comment;
import com.togedog.reply.dto.ReplyDto;
import com.togedog.reply.entity.Reply;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReplyMapper {

    default Reply replyPostToReply(ReplyDto.Post requestBody) {
        Comment comment = new Comment();
        comment.setCommentId(requestBody.getCommentId());
        Reply reply = new Reply();
        reply.setComment(comment);
        reply.setReply(requestBody.getReply());
        return reply;
    }

    Reply replyPatchToReply(ReplyDto.Patch requestBody);
    default ReplyDto.Response replyToReplyResponse(Reply reply) {
        return ReplyDto.Response.builder()
                .replyId(reply.getReplyId())
                .name(reply.getMember().getName())
                .reply(reply.getReply())
                .build();
    }
}
