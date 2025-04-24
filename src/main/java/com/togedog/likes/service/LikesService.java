package com.togedog.likes.service;

import com.togedog.board.entity.Board;
import com.togedog.board.repository.BoardRepository;
import com.togedog.exception.BusinessLogicException;
import com.togedog.exception.ExceptionCode;
import com.togedog.likes.entity.Likes;
import com.togedog.likes.repository.LikesRepository;
import com.togedog.member.entity.Member;
import com.togedog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class LikesService {
    private final LikesRepository likesRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void toggleLikes(Long memberId, Long boardId,Authentication authentication){
        //멤버가 존재하는지 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        //인증된 사용자가 memberId와 일치하는지 확인
        String email = (String) authentication.getPrincipal();
        Member authenticatedMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.UNAUTHORIZED_ACCESS));

        if (!(authenticatedMember.getMemberId() == memberId)){
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED_ACCESS);
        }

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));

        Optional<Likes> existingLikes = likesRepository.findByMemberAndBoard(member, board);
        //해당 멤버가 해당 게시물에 이미 좋아요 눌렀는지 확인
        if (existingLikes.isPresent()){
            likesRepository.delete(existingLikes.get());
            board.setLikesCount(board.getLikesCount()-1); // 좋아요 존재하면 좋아요 삭제, 좋아요수-1
        } else {
            Likes likes = new Likes();
            likes.setMember(member);
            likes.setBoard(board);
            likesRepository.save(likes);
            board.setLikesCount(board.getLikesCount()+1);// 존재하지 않으면 새로운 좋아요 생성,저장, 좋아요수+1
        }

        boardRepository.save(board);
    }
}
