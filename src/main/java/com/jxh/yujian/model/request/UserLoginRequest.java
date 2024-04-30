package com.jxh.yujian.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求体
 *
 * @author 20891
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = -8878645321233825616L;

    private String userAccount;

    private String userPassword;

}
