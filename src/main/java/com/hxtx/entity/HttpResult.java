package com.hxtx.entity;

/**
 * api请求结果通用返回格式
 * Created by dongchen on 16/3/27.
 */
public class HttpResult {
    /**
     * 请求结果状态码
     * 0 = 成功, 其他 = 失败
     */
    private int code;
    /**
     * 请求失败时的错误描述
     */
    private String msg;
    /**
     * 请求成功时的返回对象
     */
    private Object data;

    /**
     * 构建请求成功时的结果对象
     * @param data
     * @return code = 0的成功结果对象
     */
    public static HttpResult succResult(Object data){
        HttpResult result = new HttpResult();
        result.setCode(0);
        result.setData(data);
        return result;
    }

    /**
     * 构建请求失败时的结果对象
     * @param code 错误码
     * @param msg 错误提示信息
     * @return
     */
    public static HttpResult errorResult(int code, String msg){
        HttpResult result = new HttpResult();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
