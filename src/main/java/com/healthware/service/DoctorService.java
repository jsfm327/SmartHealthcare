package com.healthware.service;

import com.healthware.common.PageResult;
import com.healthware.dto.LoginDTO;
import com.healthware.entity.Doctor;
import com.healthware.vo.DoctorLoginVO;
import com.healthware.vo.DoctorVO;

import java.util.List;

public interface DoctorService {

    PageResult<DoctorVO> listDoctors(int page, int size, Long deptId);

    DoctorVO getDetail(Long id);

    List<DoctorVO> listByDepartment(Long deptId);

    void addDoctor(Doctor doctor);

    void updateDoctor(Long id, Doctor doctor);

    void deleteDoctor(Long id);

    DoctorLoginVO doctorLogin(LoginDTO dto);
}
