package com.jxh.yujian.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户加入队伍请求体
 *
 * @author 20891
 */
@Data
public class TeamJoinRequest implements Serializable {

    private static final long serialVersionUID = 1519052724282426767L;

    /**
     * id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;
}