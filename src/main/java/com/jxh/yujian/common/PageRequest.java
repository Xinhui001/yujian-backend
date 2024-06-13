package com.jxh.yujian.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页参数
 * @author 20891
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 2655320854932740776L;

    /**
     * 页面大小
     */
    protected int pageSize = 10;

    /**
     * 当前页数
     */
    protected int pageNum = 1;
}
