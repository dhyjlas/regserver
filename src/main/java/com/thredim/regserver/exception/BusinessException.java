package com.thredim.regserver.exception;

/**
 * 业务逻辑处理异常，用于处理业务中产生的逻辑错误
 *
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message, new Throwable(message));
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
