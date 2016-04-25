package com.hxtx.exception;

/**
 * 接口调用过程中的失败, 主动抛出异常
 * Created by dongchen on 16/3/28.
 */
public class ApiException extends RuntimeException{

    private String errCode;
    private String errMessage;

    public ApiException (String message) {
        super(message);
    }

    public ApiException (String errCode, String errMessage){
        super(errCode + ":" + errMessage);
        this.errCode = errCode;
        this.errMessage = errMessage;
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrCode() {
        return errCode;
    }

    public String getErrMessage() {
        return errMessage;
    }
}
