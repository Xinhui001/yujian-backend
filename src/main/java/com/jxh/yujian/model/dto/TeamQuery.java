package com.jxh.yujian.model.dto;

import com.jxh.yujian.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 队伍查询封装类
 * @TableName team
 * @author 20891
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends PageRequest implements Serializable {

    private static final long serialVersionUID = 198947739029868775L;
    /**
     * id
     */
    private Long id;

    /**
     * 队伍id 列表
     */
    private List<Long> idList;

    /**
     * 搜索关键词（同时对队伍名称和描述搜索）
     */
    private String searchText;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;
}