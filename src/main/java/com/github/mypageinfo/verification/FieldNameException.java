package com.github.mypageinfo.verification;

/*
* 用于表示 PageInfo.FieldRule 中的字段 与 实际校验类 中的字段不匹配
* */
public class FieldNameException extends Exception {

    public FieldNameException() {
        super();
    }

    public FieldNameException(String message) {
        super(message);
    }

    public FieldNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public FieldNameException(Throwable cause) {
        super(cause);
    }

    public FieldNameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + getMessage();
    }
}
