package com.healthware.service;

import com.healthware.common.PageResult;
import com.healthware.dto.LoginDTO;
import com.healthware.entity.Admin;
import com.healthware.vo.AdminVO;
import com.healthware.vo.LoginVO;
import com.healthware.vo.UserVO;

import java.util.Map;

public interface AdminService {

    LoginVO adminLogin(LoginDTO dto);

    PageResult<AdminVO> listAdmins(int page, int size);

    void addAdmin(Admin admin);

    void updateAdmin(Long id, Admin admin);

    void deleteAdmin(Long id);

    void toggleUserStatus(Long userId, int status);

    PageResult<UserVO> listUsers(int page, int size);

    Map<String, Object> getStatistics();
}
