package com.togedog.board.service;

import com.togedog.board.entity.Board;
import com.togedog.board.entity.BoardType;
import com.togedog.board.repository.BoardRepository;
import com.togedog.exception.BusinessLogicException;
import com.togedog.exception.ExceptionCode;
import com.togedog.member.entity.Member;
import com.togedog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    public BoardType convertToBoardType(String boardType) {
        try {
            return BoardType.valueOf(boardType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessLogicException(ExceptionCode.INVALID_BOARD_TYPE);
        }
    }

    public Board createBoard(Board board, BoardType boardType,Authentication authentication){
        Member member = extractMemberFromAuthentication(authentication);
        board.setMember(member);
        board.setBoardType(boardType);
        return boardRepository.save(board);
    }

    public Board patchBoard(Board board, Authentication authentication){
        extractMemberFromAuthentication(authentication);
        Board findBoard = boardRepository.findById(board.getBoardId())
                        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));

        if(!findBoard.getMember().getEmail().equals(authentication.getName())) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED_MEMBER);
        }

        Optional.ofNullable(board.getBoardType())
                .ifPresent(boardType -> findBoard.setBoardType(boardType));
        Optional.ofNullable(board.getContent())
                .ifPresent(content -> findBoard.setContent(content));
        Optional.ofNullable(board.getTitle())
                .ifPresent(title -> findBoard.setTitle(title));
        Optional.ofNullable(board.getContentImg())
                .ifPresent(contentImg -> findBoard.setContentImg(contentImg));

        findBoard.setModifiedAt(LocalDateTime.now());
        return boardRepository.save(findBoard);
    }

    public Board getBoard(Board board){
        board.incrementViewCount();
        Board findBoard = boardRepository.findById(board.getBoardId())
                .orElseThrow((
                        () -> new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND)
                ));
        return findBoard;
    }

    public Page<Board> findBoardsByType(BoardType boardType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("boardId").descending());
        return boardRepository.findAllByBoardType(boardType, pageable);
    }

    public void deleteBoard(long boardId, Authentication authentication){
        extractMemberFromAuthentication(authentication);
        Board findBoard = findVerifiedBoard(boardId);
        if(!findBoard.getMember().getEmail().equals(authentication.getName())) {
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED_MEMBER);
        }
        findBoard.setBoardStatus(Board.BoardStatus.BOARD_DELETED);
        boardRepository.save(findBoard);
    }

    //특정 id가진 게시물이 데이터베이스에 존재하는지 확인, 존재하지않으면 예외 발생
    @Transactional(readOnly = true)
    public Board findVerifiedBoard(long boardId){
        Optional<Board> findBoard =
                boardRepository.findById(boardId);
        Board result = findBoard.orElseThrow(()->
                new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));
        return result;
    }

    private Member extractMemberFromAuthentication(Authentication authentication) {
        if (authentication.getPrincipal() == null) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }
        String email = (String) authentication.getPrincipal();
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }
}