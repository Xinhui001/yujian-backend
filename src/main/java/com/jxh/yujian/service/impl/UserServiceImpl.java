package com.jxh.yujian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jxh.yujian.common.ErrorCode;
import com.jxh.yujian.exception.BusinessException;
import com.jxh.yujian.mapper.UserMapper;
import com.jxh.yujian.model.domain.User;
import com.jxh.yujian.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.jxh.yujian.constant.UserConstant.ADMIN_ROLE;
import static com.jxh.yujian.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author 20891
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2024-04-01 22:20:47
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    /**
     * 盐值
     */
    private static final String SALT = "jxh";


    @Resource
    private UserMapper userMapper;

    /**
     * 用户注册服务实现
     *
     * @param userAccount   账户
     * @param userPassword  密码
     * @param checkPassword 校验密码
     * @return 用户id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {

        //账户、密码、校验码不为空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        //账户长度不小于4
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户长度不小于4");
        }
        //账户中不得包含特殊字符
        String validPattern = "[ _`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\\n|\\r|\\t";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户中不得包含特殊字符");
        }
        //密码和校验码不小于8
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码和校验码不小于8");
        }
        //密码和校验码要相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码和校验码要相同");
        }
        //账户不可重复
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        Long selectCount = userMapper.selectCount(queryWrapper);
        if (selectCount > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户不可重复");
        }

        //对密码加密
//        final String SALT = "jxh";
        String safetyPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //存入数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(safetyPassword);
        int saveResult = userMapper.insert(user);
        if (saveResult < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "存入数据库失败");
        }

        return user.getId();
    }

    /**
     * 用户登录服务实现
     *
     * @param userAccount
     * @param userPassword
     * @param request
     * @return 用户脱敏信息
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {

        //账户、密码、校验码不为空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        //账户长度不小于4
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户长度不小于4");
        }
        //账户中不得包含特殊字符
        String validPattern = "[ _`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\\n|\\r|\\t";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户中不得包含特殊字符");
        }
        //密码和校验码不小于8
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码和校验码不小于8");
        }
        String encodePassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        userQueryWrapper.eq("userPassword", encodePassword);
        User user = userMapper.selectOne(userQueryWrapper);
        if (user == null) {
            log.info("user login failed,userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        //脱敏用户信息
        User safetyUser = getSafetyUser(user);
        //记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);

        //返回用户脱敏信息
        return safetyUser;
    }

    /**
     * 用户脱敏
     *
     * @param user 未脱敏用户信息
     * @return 脱敏用户信息
     */
    @Override
    public User getSafetyUser(User user) {

        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setTags(user.getTags());
        safetyUser.setProfile(user.getProfile());
        safetyUser.setCreateTime(user.getCreateTime());
        return safetyUser;
    }

    /**
     * 退出登录
     *
     * @param request 获取session信息
     * @return
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 查询标签
     *
     * @param listTagName 标签名字  json格式
     * @return 用户list
     */
    @Override
    public List<User> searchUsersByTags(List<String> listTagName) {
        if (CollectionUtils.isEmpty(listTagName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签列表参数为空");
        }
        /**
         * 1.第一种方式 内存计算
         *
         * 可以通过并发进一步优化
         * 可以与SQL查询相结合，谁先返回就用谁
         */
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //1.先查询所有用户
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        //2.判断内存中是否包含要求的标签
        return userList.stream().filter(user -> {
            String tagStr = user.getTags();
            if (StringUtils.isBlank(tagStr)) {
                return false;
            }
            //反序列化为集合
            Set<String> tempTagNameSet = gson.fromJson(tagStr, new TypeToken<Set<String>>() {
            }.getType());
            //集合判空(空指针异常)
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            //在传入的listTagName中查找是否包含有数据库中包含的标签
            for (String tagName : listTagName) {
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());


        /**
         * 2.第二种方式 SQL查询
         * //拼接查询
         *         QueryWrapper<User> queryWrapper = new QueryWrapper<>();
         *         for (String name : listTagName) {
         *             queryWrapper.like("tags", name);
         *         }
         *         List<User> users = userMapper.selectList(queryWrapper);
         *
         *         return users.stream().map(this::getSafetyUser).collect(Collectors.toList());
         */

    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        //仅管理员可查询
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);

        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

    /**
     * 是否为管理员
     *
     * @param loginUser
     * @return
     */
    @Override
    public boolean isAdmin(User loginUser) {
        return loginUser != null && loginUser.getUserRole() == ADMIN_ROLE;
    }

    /**
     * 修改用户信息
     *
     * @param user      要修改的用户对象(前端传过来的)
     * @param loginUser 当前登录的用户  可能是管理员也可能是用户本人
     * @return
     */
    @Override
    public int updateUser(User user, User loginUser) {
        long userId = user.getId();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //如果是管理员，允许更新任意用户
        //如果不是管理员，只允许修改自己的信息
        if (!isAdmin(loginUser) && userId != loginUser.getId()) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        //根据要修改的用户id，查询数据库中是否存在该用户
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"数据库中不存在当前要修改的用户");
        }
        //执行修改
        return userMapper.updateById(user);
    }

    /**
     * 获取当前用户信息
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return (User) userObj;
    }

}




