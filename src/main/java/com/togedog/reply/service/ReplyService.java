package com.togedog.reply.service;

import com.togedog.comment.entity.Comment;
import com.togedog.comment.repository.CommentRepository;
import com.togedog.exception.BusinessLogicException;
import com.togedog.exception.ExceptionCode;
import com.togedog.member.entity.Member;
import com.togedog.member.repository.MemberRepository;
import com.togedog.reply.entity.Reply;
import com.togedog.reply.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    public Reply createReply(Reply reply, Authentication authentication) {
        Member member = extractMemberFromAuthentication(authentication);
        Comment comment = commentRepository.findById(reply.getComment().getCommentId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
        reply.setMember(member);
        reply.setComment(comment);
        return replyRepository.save(reply);
    }

    public Reply updateReply(Reply reply, Authentication authentication) {
        extractMemberFromAuthentication(authentication);
        Reply findReply = findVerifiedReply(reply.getReplyId());
        if(!findReply.getMember().getEmail().equals(authentication.getName())) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED_MEMBER);
        }
        Optional.ofNullable(reply.getReply())
                .ifPresent(replies -> findReply.setReply(replies));

        if(reply.getComment() != null) {
            findReply.setComment(reply.getComment());
        }
        return replyRepository.save(findReply);
    }

    public void deleteReply(long replyId, Authentication authentication) {
        Member member = extractMemberFromAuthentication(authentication);
        Reply verifiedReply = findVerifiedReply(replyId);
        if(!verifiedReply.getMember().getEmail().equals(member.getEmail())) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED_MEMBER);
        }
        verifiedReply.setMember(member);
        verifiedReply.setReplyStatus(Reply.ReplyStatus.REPLY_DELETED);
        replyRepository.save(verifiedReply);
    }

    @Transactional(readOnly = true)
    public Reply findVerifiedReply(long replyId) {
        Optional<Reply> findreply = replyRepository.findById(replyId);
        Reply resultReply = findreply.orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
        return resultReply;
    }

    private Member extractMemberFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }
        String email = (String) authentication.getPrincipal();
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }
}
