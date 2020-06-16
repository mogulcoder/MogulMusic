package com.mogul.lib_network.okhttp.exception;

public class OKHttpException extends Exception{
    private static final long serialVersionUID = 1L;

    private int code;

    private Object msg;

    public OKHttpException(int code, Object msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public Object getMsg() {
        return msg;
    }
}
