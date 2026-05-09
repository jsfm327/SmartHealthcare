package com.healthware.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.healthware.common.Constants;
import com.healthware.common.PageResult;
import com.healthware.dto.LoginDTO;
import com.healthware.entity.Admin;
import com.healthware.entity.User;
import com.healthware.mapper.AdminMapper;
import com.healthware.mapper.UserMapper;
import com.healthware.service.impl.AdminServiceImpl;
import com.healthware.utils.JwtUtil;
import com.healthware.utils.PasswordUtil;
import com.healthware.utils.RedisUtil;
import com.healthware.vo.AdminVO;
import com.healthware.vo.LoginVO;
import com.healthware.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminMapper adminMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RedisUtil redisUtil;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Admin testAdmin;

    @BeforeEach
    void setUp() {
        testAdmin = new Admin();
        testAdmin.setId(1L);
        testAdmin.setUsername("admin");
        testAdmin.setPassword(PasswordUtil.encrypt("admin123"));
        testAdmin.setRealName("系统管理员");
        testAdmin.setStatus(Constants.STATUS_NORMAL);
    }

    @Test
    void adminLogin_Success() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("admin");
        dto.setPassword("admin123");

        when(adminMapper.selectByUsername("admin")).thenReturn(testAdmin);
        when(jwtUtil.generateToken(1L, "admin", "admin")).thenReturn("admin-token");

        LoginVO result = adminService.adminLogin(dto);

        assertNotNull(result);
        assertEquals("admin-token", result.getToken());
        assertEquals(1L, result.getUserId());
        assertEquals("admin", result.getUsername());
    }

    @Test
    void listAdmins_Success() {
        List<Admin> admins = Arrays.asList(testAdmin);
        when(adminMapper.selectPage(any(), any())).thenReturn(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<Admin>(1, 10).setRecords(admins));

        PageResult<AdminVO> result = adminService.listAdmins(1, 10);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    @Test
    void addAdmin_Success() {
        Admin admin = new Admin();
        admin.setUsername("newadmin");
        admin.setPassword("admin123");
        admin.setRealName("新管理员");

        when(adminMapper.insert(any(Admin.class))).thenReturn(1);

        assertDoesNotThrow(() -> adminService.addAdmin(admin));
        verify(adminMapper).insert(any(Admin.class));
    }

    @Test
    void toggleUserStatus_Success() {
        User user = new User();
        user.setId(1L);
        user.setStatus(Constants.STATUS_NORMAL);

        when(userMapper.selectById(1L)).thenReturn(user);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        assertDoesNotThrow(() -> adminService.toggleUserStatus(1L, Constants.STATUS_DISABLED));
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    void getStatistics_Success() {
        when(adminMapper.selectCount(any())).thenReturn(5L);
        when(userMapper.selectCount(any())).thenReturn(100L);

        Map<String, Object> stats = adminService.getStatistics();

        assertNotNull(stats);
        assertTrue(stats.containsKey("totalAdmins"));
        assertTrue(stats.containsKey("totalUsers"));
    }
}
