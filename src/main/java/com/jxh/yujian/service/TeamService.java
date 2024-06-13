package com.jxh.yujian.service;

import com.jxh.yujian.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jxh.yujian.model.domain.User;

/**
* @author 20891
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-06-13 12:30:19
*/
public interface TeamService extends IService<Team> {

    /**
     * 添加队伍
     *
     * @param team
     * @param loginUser
     * @return
     */
    long save(Team team, User loginUser);
}
