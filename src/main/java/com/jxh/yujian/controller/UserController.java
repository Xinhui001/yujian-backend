package com.jxh.yujian.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxh.yujian.common.BaseResponse;
import com.jxh.yujian.common.ErrorCode;
import com.jxh.yujian.common.ResultUtils;
import com.jxh.yujian.constant.UserConstant;
import com.jxh.yujian.exception.BusinessException;
import com.jxh.yujian.model.domain.User;
import com.jxh.yujian.model.request.UserLoginRequest;
import com.jxh.yujian.model.request.UserRegisterRequest;
import com.jxh.yujian.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户接口
 *
 * @author 20891
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:5173/"})
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {

        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);

        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {

        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);

        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {

        if (request == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        int result = userService.userLogout(request);

        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> currentUser(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        //TODO 校验用户是否合法
        long userId = user.getId();
        User newUser = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(newUser);

        return ResultUtils.success(safetyUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest servletRequest) {
        if (!isAdmin(servletRequest)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            userQueryWrapper.like("username", username);
        }
        List<User> userList = userService.list(userQueryWrapper);
        List<User> result = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest servletRequest) {
        if (!isAdmin(servletRequest)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.removeById(id);

        return ResultUtils.success(result);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersByTags(@RequestBody(required = false) List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUsersByTags(tagNameList);
        return ResultUtils.success(userList);
    }


    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        return user != null && user.getUserRole() == UserConstant.ADMIN_ROLE;
    }

}
