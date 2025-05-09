package com.togedog.exception;

import lombok.Getter;

public class BusinessLogicException extends RuntimeException {
    @Getter
    private ExceptionCode exceptionCode;

    public BusinessLogicException(ExceptionCode exceptionCode) {
        super(exceptionCode.getStatusDescription());
        this.exceptionCode = exceptionCode;
    }


}
