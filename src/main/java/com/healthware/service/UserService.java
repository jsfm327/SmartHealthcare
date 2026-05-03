package com.healthware.service;

import com.healthware.dto.LoginDTO;
import com.healthware.dto.RegisterDTO;
import com.healthware.dto.UserDTO;
import com.healthware.vo.LoginVO;
import com.healthware.vo.UserVO;

public interface UserService {

    LoginVO login(LoginDTO dto);

    void register(RegisterDTO dto);

    void resetPassword(String phone, String newPassword);

    UserVO getProfile(Long userId);

    void updateProfile(Long userId, UserDTO dto);

    void changePassword(Long userId, String oldPwd, String newPwd);

    void handleLoginFail(Long userId);

    void resetLoginFailCount(Long userId);
}
