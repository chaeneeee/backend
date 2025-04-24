package com.togedog.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ExceptionCode {

    //추가해야할 에러 코드
    NOT_MATCH_HOST_MEMBER(404,"NOT_MATCH_HOST_MEMBER"),
    REQUEST_ALREADY_RECEIVED_FROM_OTHER_PARTY(202,"NOT_MATCH_HOST_MEMBER"),
    ACCESS_DENIED(403,"ACCESS_DENIED"),
    CHATROOM_NOT_FOUND(404,"CHATROOM_NOT_FOUND"),

    //match 관련
    MATCH_NOT_FOUND(404,"MATCH_NOT_FOUND"),
    MATCH_ALREADY_EXISTS(409,"MATCH_ALREADY_EXISTS"),

    //board 관련
    BOARD_NOT_FOUND(404,"Board Not Found"),
    BOARD_EXISTS(409,"Board exists"),
    INVALID_BOARD_TYPE(400,"INVALID_BOARD_TYPE"),

    //comment 관련
    COMMENT_NOT_FOUND(404,"COMMENT_NOT_FOUND"),
    //member 관련
    MEMBER_NOT_FOUND(404,"Member Not Found"),
    MEMBER_EXISTS(409,"Member exists"),
    PHONE_EXISTS(409, "Phone exists"),
    NICKNAME_EXISTS(409, "NickName exists"),

    //friend 관련
    FRIEND_REQUEST_NOT_FOUND(404, "Request Not Found"),

    //Pet 관련
    PET_NOT_FOUND(404, "Pet Not Found"),
    PET_EXISTS(409,"Pet exists"),

    //친구 요청 관련
    UNAUTHORIZED_ACCESS(404, "친구요청을 없습니다."),

    //메일 전송 관련
    UNABLE_TO_SEND_EMAIL(404, "이메일 전송에 실패했습니다."),
    NO_SUCH_ALGORITHM(404, "No_Such_Algorithm"),

    //토큰 인증 관련
    UNAUTHORIZED_MEMBER(401, "권한이 없는 멤버입니다."),
    TOKEN_INVALID(404, "토큰값이 유효하지 않습니다.");

    @Getter
    private int statusCode;

    @Getter
    private String statusDescription;


}