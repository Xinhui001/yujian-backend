package com.jxh.yujian.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用删除请求
 * @author 20891
 */
@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = 3644544238511674362L;

    private long id;
}