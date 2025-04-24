package com.togedog.board.controller;

import com.togedog.auth.service.AuthService;
import com.togedog.board.dto.BoardDto;
import com.togedog.board.entity.Board;
import com.togedog.board.entity.BoardType;
import com.togedog.board.mapper.BoardMapper;
import com.togedog.board.service.BoardService;
import com.togedog.dto.MultiResponseDto;
import com.togedog.dto.SingleResponseDto;
import com.togedog.utils.UriCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/boards")
@Validated
@RequiredArgsConstructor
public class BoardController {
    private final static String BOARD_DEF_URL = "/boards";
    private final BoardMapper mapper;
    private final BoardService service;
    private final AuthService authService;

    @PostMapping("/{board-type}")
    public ResponseEntity postBoard(@PathVariable("board-type") String boardType,
                                    @Valid @RequestBody BoardDto.Post requestBody,
                                    Authentication authentication) {
        BoardType enumBoardType =
                service.convertToBoardType(boardType);
        Board createBoard =
                service.createBoard(mapper.boardDtoPostToBoard(requestBody), enumBoardType, authentication);
        URI location = UriCreator.createUri(BOARD_DEF_URL, createBoard.getBoardId());

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{board-id}")
    public ResponseEntity getBoard(@PathVariable("board-id")
                                   @Positive long boardId) {
        Board findBoard = service.getBoard(service.findVerifiedBoard(boardId));

        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.boardToBoardDtoResponse(findBoard)),
                HttpStatus.OK);
    }

    @PatchMapping("/{board-id}")
    public ResponseEntity patchBoard(@PathVariable("board-id") @Positive long boardId,
                                     @Valid @RequestBody BoardDto.Patch requestBody,
                                     Authentication authentication){
        String email = null;
        if (authentication != null) {
            email = (String) authentication.getPrincipal();
            boolean isLoggedOut = !authService.isTokenValid(email);
            if (isLoggedOut) {
                return new ResponseEntity<>("User Logged Out", HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        requestBody.setBoardId(boardId);
        Board patchBoard = service.patchBoard(mapper.boardDtoPatchToBoard(requestBody), authentication);

        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.boardToBoardDtoResponse(patchBoard)),
                HttpStatus.OK);
    }

    @DeleteMapping("/{board-id}")
    public ResponseEntity deleteBoard(@PathVariable("board-id") @Positive long boardId,
                                      Authentication authentication) {
        String email = null;
        if (authentication != null) {
            email = (String) authentication.getPrincipal();
            boolean isLoggedOut = !authService.isTokenValid(email);
            if (isLoggedOut) {
                return new ResponseEntity<>("User Logged Out", HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        service.deleteBoard(boardId, authentication);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity getBoards(@RequestParam(name = "type") String boardType,
                                    @Positive @RequestParam int page,
                                    @Positive @RequestParam int size){
        BoardType enumBoardType;
        try {
            enumBoardType = service.convertToBoardType(boardType);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid board type: " + boardType, HttpStatus.BAD_REQUEST);
        }
        Page<Board> pageBoards = service.findBoardsByType(enumBoardType, page - 1, size);
        List<Board> boards = pageBoards.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(mapper.boardToBoardDtoResponses(boards), pageBoards),
                HttpStatus.OK);
    }
}