package com.jxh.yujian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxh.yujian.common.ErrorCode;
import com.jxh.yujian.exception.BusinessException;
import com.jxh.yujian.model.domain.Team;
import com.jxh.yujian.model.domain.User;
import com.jxh.yujian.model.domain.UserTeam;
import com.jxh.yujian.model.dto.TeamQuery;
import com.jxh.yujian.model.enums.TeamStatusEnum;
import com.jxh.yujian.model.request.TeamUpdateRequest;
import com.jxh.yujian.model.vo.TeamUserVO;
import com.jxh.yujian.model.vo.UserVO;
import com.jxh.yujian.service.TeamService;
import com.jxh.yujian.mapper.TeamMapper;
import com.jxh.yujian.service.UserService;
import com.jxh.yujian.service.UserTeamService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
* @author 20891
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2024-06-13 12:30:19
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

    /**
     * 添加队伍
     * 为了防止插入队伍表或用户队伍关系表发生错误  使用事务注解
     * 要么都插入成功 有一个插入失败就回滚
     *
     * @param team
     * @param loginUser
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long save(Team team, User loginUser) {
//        1. 请求参数是否为空？
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
//        2. 是否登录，未登录不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
//        3. 校验信息
        final long userId = loginUser.getId();
//        a. 队伍人数 > 2 且 <= 10
        Integer teamMaxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (teamMaxNum < 3 || teamMaxNum > 10) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数不符合要求");
        }
//        b. 队伍标题 <= 20
        String teamName = team.getName();
        if (StringUtils.isBlank(teamName) || teamName.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍名字不符合要求");
        }
//        c. 描述 <= 512
        String teamDescription = team.getDescription();
        if (StringUtils.isBlank(teamDescription) && teamDescription.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍描述不符合要求");
        }
//        d. status 是否公开（int）不传默认为 0（公开）
        //第一个是status是判断传参是否为null，第二个statusEnum是判断用户输入队伍的状态态属性status是否合法存在（0/1/2）
        Integer teamStatus = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(teamStatus);
        if (teamStatusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态不符合要求");
        }
//        e. 如果 status 是加密状态，一定要有密码，且密码 <= 32
        String teamPassword = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(teamStatusEnum)) {
            if (StringUtils.isBlank(teamPassword) || teamPassword.length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不符合要求");
            }
        }
//        f. 超时时间 > 当前时间
        Date teamExpireTime = team.getExpireTime();
        if (new Date().after(teamExpireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"超时时间小于当前时间");
        }
//        g. 校验用户最多创建 5 个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long hasTeamCount = this.count(queryWrapper);
        if (hasTeamCount >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"最多只能创建5跟队伍");
        }
//        4. 插入队伍信息到队伍表
        //插入时把teamId设置为null 因为数据库自增
        team.setId(null);
        team.setUserId(userId);
        boolean result = this.save(team);
        Long teamId = team.getId();
        if (teamId == null || !result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"创建队伍失败");
        }
//        5. 插入用户 => 队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(teamId);
        userTeam.setUserId(userId);
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"创建队伍失败");
        }
        return teamId;
    }

    /**
     * 搜索队伍
     *
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
//        1. 从请求参数中取出队伍名称等查询条件，如果存在则作为查询条件
        //id
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id",id);
            }
        }
        //id列表
        List<Long> idList = teamQuery.getIdList();
        if (!CollectionUtils.isEmpty(idList)) {
            queryWrapper.in("id",idList);
        }
        //searchText   可以通过某个关键词同时对名称和描述查询
        String searchText = teamQuery.getSearchText();
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("name", searchText).or().like("description",searchText));
        }
        //name
        String name = teamQuery.getName();
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like("name",name);
        }
        //description
        String description = teamQuery.getDescription();
        if (StringUtils.isNotBlank(description)) {
            queryWrapper.like("description",description);
        }
        //maxNum   最大人数相等的  最大人数至少为3人
        Integer maxNum = teamQuery.getMaxNum();
        if (maxNum != null && maxNum > 2) {
            queryWrapper.eq("maxNum",maxNum);
        }
        //userId  根据创建人查询
        Long userId = teamQuery.getUserId();
        if (userId != null && userId > 0) {
            queryWrapper.eq("userId",userId);
        }
        //status
        Integer status = teamQuery.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        //如果status不为 0，1，2  则设置为公开队伍
        if (teamStatusEnum == null) {
            teamStatusEnum = TeamStatusEnum.PUBLIC;
        }
        //如果不是管理员 并且队伍私有  则无权限查询  只有管理员才能查看加密还有非公开的房间
        if (!isAdmin && TeamStatusEnum.PRIVATE.equals(teamStatusEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        queryWrapper.eq("status",teamStatusEnum.getValue());
//        2. 不展示已过期的队伍（根据过期时间筛选）
        // sql   expireTime is null or expireTime > now()
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
//        5. 关联查询已加入队伍的用户信息
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        for (Team team : teamList) {
            Long id = team.getUserId();
            //id为空 则跳出当前循环剩余部分 进行下一次循环迭代
            if (id == null) {
                continue;
            }
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            //给user脱敏
            User user = userService.getById(id);
            if (user != null) {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    /**
     * 更新队伍
     *
     * @param team
     * @param loginUser
     * @return
     */
    @Override
    public boolean updateTeam(TeamUpdateRequest team, User loginUser) {
//        1. 判断请求参数是否为空
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = team.getId();
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        2. 查询队伍是否存在
        Team oldTeam = this.getById(id);
        if (oldTeam == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
//        3. 只有管理员或者队伍的创建者可以修改
        if (oldTeam.getUserId() != loginUser.getId() && userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
//        4. TODO 如果用户传入的新值和老值一致，就不用 update 了 (降低数据库使用次数)
//        5. 如果队伍状态改为加密，必须要有密码
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(team.getStatus());
        if (TeamStatusEnum.SECRET.equals(teamStatusEnum)) {
            if (StringUtils.isBlank(team.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加密队伍需要密码");
            }
        }
//        6. 更新成功
        Team newTeam = new Team();
        BeanUtils.copyProperties(team, newTeam);
        return this.updateById(newTeam);
    }
}




