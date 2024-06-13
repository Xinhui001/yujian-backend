package com.jxh.yujian.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户退出队伍请求体
 *
 * @author 20891
 */
@Data
public class TeamQuitRequest implements Serializable {

    private static final long serialVersionUID = -6832795319026646108L;

    /**
     * id
     */
    private Long teamId;

}