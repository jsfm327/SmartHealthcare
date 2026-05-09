package com.healthware.service.impl;

import com.healthware.common.Constants;
import com.healthware.common.ResultCode;
import com.healthware.dto.LoginDTO;
import com.healthware.dto.RegisterDTO;
import com.healthware.dto.UserDTO;
import com.healthware.entity.User;
import com.healthware.exception.BusinessException;
import com.healthware.mapper.UserMapper;
import com.healthware.service.UserService;
import com.healthware.utils.JwtUtil;
import com.healthware.utils.PasswordUtil;
import com.healthware.utils.RedisUtil;
import com.healthware.vo.LoginVO;

import java.time.LocalDateTime;
import com.healthware.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = userMapper.selectByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (user.getStatus() == Constants.STATUS_DISABLED) {
            throw new BusinessException(ResultCode.USER_LOCKED);
        }
        if (!PasswordUtil.verify(dto.getPassword(), user.getPassword())) {
            handleLoginFail(user.getId());
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        resetLoginFailCount(user.getId());

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), "user");
        redisUtil.set(Constants.USER_TOKEN_PREFIX + user.getId(), token, 2, TimeUnit.HOURS);

        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        return vo;
    }

    @Override
    public void register(RegisterDTO dto) {
        if (userMapper.selectByUsername(dto.getUsername()) != null) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }
        if (dto.getPhone() != null && userMapper.selectByPhone(dto.getPhone()) != null) {
            throw new BusinessException(ResultCode.PHONE_EXISTS);
        }

        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(PasswordUtil.encrypt(dto.getPassword()));
        user.setStatus(Constants.STATUS_NORMAL);
        user.setLoginFailCount(0);
        userMapper.insert(user);
    }

    @Override
    public void resetPassword(String phone, String newPassword) {
        User user = userMapper.selectByPhone(phone);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        user.setPassword(PasswordUtil.encrypt(newPassword));
        userMapper.updateById(user);
    }

    @Override
    public UserVO getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    public void updateProfile(Long userId, UserDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        BeanUtils.copyProperties(dto, user, "id", "username", "password", "status", "loginFailCount", "lockTime");
        userMapper.updateById(user);
    }

    @Override
    public void changePassword(Long userId, String oldPwd, String newPwd) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (!PasswordUtil.verify(oldPwd, user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        user.setPassword(PasswordUtil.encrypt(newPwd));
        userMapper.updateById(user);
    }

    @Override
    public void handleLoginFail(Long userId) {
        User user = userMapper.selectById(userId);
        int count = user.getLoginFailCount() + 1;
        userMapper.updateLoginFailCount(userId, count);
        if (count >= Constants.MAX_LOGIN_FAIL_COUNT) {
            user.setStatus(Constants.STATUS_DISABLED);
            user.setLockTime(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }

    @Override
    public void resetLoginFailCount(Long userId) {
        userMapper.updateLoginFailCount(userId, 0);
    }
}
