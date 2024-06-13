package com.jxh.yujian.service;

import com.jxh.yujian.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jxh.yujian.model.domain.User;
import com.jxh.yujian.model.dto.TeamQuery;
import com.jxh.yujian.model.request.TeamJoinRequest;
import com.jxh.yujian.model.request.TeamQuitRequest;
import com.jxh.yujian.model.request.TeamUpdateRequest;
import com.jxh.yujian.model.vo.TeamUserVO;

import java.util.List;

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

    /**
     * 搜索队伍
     *
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 更新队伍
     *
     * @param team
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest team, User loginUser);

    /**
     * 加入队伍
     *
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     *
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 删除队伍
     *
     * @param id
     * @param loginUser
     * @return
     */
    boolean deleteTeam(long id, User loginUser);
}
