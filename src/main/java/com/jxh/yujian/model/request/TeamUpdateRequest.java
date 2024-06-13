package com.jxh.yujian.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍更新请求体
 *
 * @author 20891
 */
@Data
public class TeamUpdateRequest implements Serializable {

    private static final long serialVersionUID = -8147401511193227981L;

    /**
     * id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;
}