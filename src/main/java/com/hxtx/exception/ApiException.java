package com.hxtx.exception;

/**
 * 接口调用过程中的失败, 主动抛出异常
 * Created by dongchen on 16/3/28.
 */
public class ApiException extends RuntimeException{

    public ApiException (String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
