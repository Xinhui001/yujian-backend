package com.jxh.yujian.exception;

import com.jxh.yujian.common.ErrorCode;

import java.io.Serializable;

/**
 * 自定义异常类
 *
 * @author 20891
 */
public class BusinessException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -3659757638249112718L;
    private final int code;

    private final String description;

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
