package com.github.mypageinfo.verification;

/*
* 用于表示 PageInfo.FieldRule 中的字段数据类型 与 实际校验类中的字段数据类型 不匹配
* */
public class FieldTypeException extends Exception {
    public FieldTypeException() {
    }

    public FieldTypeException(String message) {
        super(message);
    }

    public FieldTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public FieldTypeException(Throwable cause) {
        super(cause);
    }

    public FieldTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + getMessage();
    }
}
