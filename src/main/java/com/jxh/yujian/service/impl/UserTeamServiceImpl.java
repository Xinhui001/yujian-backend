package com.jxh.yujian.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxh.yujian.model.domain.UserTeam;
import com.jxh.yujian.service.UserTeamService;
import com.jxh.yujian.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 20891
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-06-13 12:30:35
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




