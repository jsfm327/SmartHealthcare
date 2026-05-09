package com.healthware.service;

import com.healthware.common.Constants;
import com.healthware.common.ResultCode;
import com.healthware.dto.LoginDTO;
import com.healthware.dto.RegisterDTO;
import com.healthware.dto.UserDTO;
import com.healthware.entity.User;
import com.healthware.exception.BusinessException;
import com.healthware.mapper.UserMapper;
import com.healthware.service.impl.UserServiceImpl;
import com.healthware.utils.JwtUtil;
import com.healthware.utils.PasswordUtil;
import com.healthware.utils.RedisUtil;
import com.healthware.vo.LoginVO;
import com.healthware.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RedisUtil redisUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword(PasswordUtil.encrypt("password123"));
        testUser.setRealName("测试用户");
        testUser.setPhone("13800138000");
        testUser.setEmail("test@example.com");
        testUser.setGender(1);
        testUser.setStatus(Constants.STATUS_NORMAL);
        testUser.setLoginFailCount(0);
    }

    @Test
    void login_Success() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");

        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);
        when(jwtUtil.generateToken(1L, "testuser", "user")).thenReturn("test-token");

        LoginVO result = userService.login(dto);

        assertNotNull(result);
        assertEquals("test-token", result.getToken());
        assertEquals(1L, result.getUserId());
        assertEquals("testuser", result.getUsername());
        verify(redisUtil).set(anyString(), anyString(), anyLong(), any());
    }

    @Test
    void login_UserNotFound() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("nonexistent");
        dto.setPassword("password123");

        when(userMapper.selectByUsername("nonexistent")).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.login(dto));
        assertEquals(ResultCode.USER_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    void login_UserDisabled() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");

        testUser.setStatus(Constants.STATUS_DISABLED);
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.login(dto));
        assertEquals(ResultCode.USER_LOCKED.getCode(), exception.getCode());
    }

    @Test
    void login_WrongPassword() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("wrongpassword");

        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateLoginFailCount(anyLong(), anyInt())).thenReturn(1);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.login(dto));
        assertEquals(ResultCode.PASSWORD_ERROR.getCode(), exception.getCode());
    }

    @Test
    void register_Success() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("newuser");
        dto.setPassword("password123");
        dto.setRealName("新用户");
        dto.setPhone("13900139000");

        when(userMapper.selectByUsername("newuser")).thenReturn(null);
        when(userMapper.selectByPhone("13900139000")).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        assertDoesNotThrow(() -> userService.register(dto));
        verify(userMapper).insert(any(User.class));
    }

    @Test
    void register_UsernameExists() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");

        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.register(dto));
        assertEquals(ResultCode.USERNAME_EXISTS.getCode(), exception.getCode());
    }

    @Test
    void getProfile_Success() {
        when(userMapper.selectById(1L)).thenReturn(testUser);

        UserVO result = userService.getProfile(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("测试用户", result.getRealName());
    }

    @Test
    void getProfile_UserNotFound() {
        when(userMapper.selectById(999L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.getProfile(999L));
        assertEquals(ResultCode.USER_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    void updateProfile_Success() {
        UserDTO dto = new UserDTO();
        dto.setRealName("更新姓名");
        dto.setPhone("13900139001");

        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        assertDoesNotThrow(() -> userService.updateProfile(1L, dto));
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    void changePassword_Success() {
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        assertDoesNotThrow(() -> userService.changePassword(1L, "password123", "newpassword"));
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    void changePassword_WrongOldPassword() {
        when(userMapper.selectById(1L)).thenReturn(testUser);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.changePassword(1L, "wrongpassword", "newpassword"));
        assertEquals(ResultCode.PASSWORD_ERROR.getCode(), exception.getCode());
    }

    @Test
    void resetPassword_Success() {
        when(userMapper.selectByPhone("13800138000")).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        assertDoesNotThrow(() -> userService.resetPassword("13800138000", "newpassword"));
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    void resetPassword_PhoneNotFound() {
        when(userMapper.selectByPhone("99999999999")).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.resetPassword("99999999999", "newpassword"));
        assertEquals(ResultCode.USER_NOT_FOUND.getCode(), exception.getCode());
    }
}
