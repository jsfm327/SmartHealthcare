package com.healthware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthware.common.Constants;
import com.healthware.common.PageResult;
import com.healthware.common.ResultCode;
import com.healthware.dto.LoginDTO;
import com.healthware.entity.Admin;
import com.healthware.entity.User;
import com.healthware.exception.BusinessException;
import com.healthware.mapper.AdminMapper;
import com.healthware.mapper.DepartmentMapper;
import com.healthware.mapper.DoctorMapper;
import com.healthware.mapper.RegistrationMapper;
import com.healthware.mapper.UserMapper;
import com.healthware.service.AdminService;
import com.healthware.utils.JwtUtil;
import com.healthware.utils.PasswordUtil;
import com.healthware.utils.RedisUtil;
import com.healthware.vo.AdminVO;
import com.healthware.vo.LoginVO;
import com.healthware.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private RegistrationMapper registrationMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public LoginVO adminLogin(LoginDTO dto) {
        Admin admin = adminMapper.selectByUsername(dto.getUsername());
        if (admin == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (admin.getStatus() == Constants.STATUS_DISABLED) {
            throw new BusinessException(ResultCode.USER_LOCKED);
        }
        if (!PasswordUtil.verify(dto.getPassword(), admin.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        String token = jwtUtil.generateToken(admin.getId(), admin.getUsername(), "admin");
        redisUtil.set(Constants.ADMIN_TOKEN_PREFIX + admin.getId(), token, 2, TimeUnit.HOURS);

        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(admin.getId());
        vo.setUsername(admin.getUsername());
        vo.setRealName(admin.getRealName());
        return vo;
    }

    @Override
    public PageResult<AdminVO> listAdmins(int page, int size) {
        Page<Admin> pageParam = new Page<>(page, size);
        Page<Admin> result = adminMapper.selectPage(pageParam, null);
        List<AdminVO> records = result.getRecords().stream().map(admin -> {
            AdminVO vo = new AdminVO();
            BeanUtils.copyProperties(admin, vo);
            return vo;
        }).collect(Collectors.toList());
        return new PageResult<>(records, result.getTotal(), page, size);
    }

    @Override
    public void addAdmin(Admin admin) {
        admin.setPassword(PasswordUtil.encrypt(admin.getPassword()));
        admin.setStatus(Constants.STATUS_NORMAL);
        adminMapper.insert(admin);
    }

    @Override
    public void updateAdmin(Long id, Admin admin) {
        admin.setId(id);
        if (admin.getPassword() != null) {
            admin.setPassword(PasswordUtil.encrypt(admin.getPassword()));
        }
        adminMapper.updateById(admin);
    }

    @Override
    public void deleteAdmin(Long id) {
        adminMapper.deleteById(id);
    }

    @Override
    public void toggleUserStatus(Long userId, int status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public PageResult<UserVO> listUsers(int page, int size) {
        Page<User> pageParam = new Page<>(page, size);
        Page<User> result = userMapper.selectPage(pageParam, null);
        List<UserVO> records = result.getRecords().stream().map(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            return vo;
        }).collect(Collectors.toList());
        return new PageResult<>(records, result.getTotal(), page, size);
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userMapper.selectCount(null));
        stats.put("doctorCount", doctorMapper.selectCount(null));
        stats.put("departmentCount", departmentMapper.selectCount(null));
        java.time.LocalDate today = java.time.LocalDate.now();
        Long todayCount = registrationMapper.selectCount(
                new LambdaQueryWrapper<com.healthware.entity.Registration>()
                        .eq(com.healthware.entity.Registration::getRegistrationDate, today));
        stats.put("todayRegistrationCount", todayCount);
        return stats;
    }
}
