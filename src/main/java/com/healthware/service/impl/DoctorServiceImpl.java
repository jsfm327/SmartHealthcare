package com.healthware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthware.common.Constants;
import com.healthware.common.PageResult;
import com.healthware.common.ResultCode;
import com.healthware.dto.LoginDTO;
import com.healthware.entity.Doctor;
import com.healthware.exception.BusinessException;
import com.healthware.mapper.DoctorMapper;
import com.healthware.service.DoctorService;
import com.healthware.utils.JwtUtil;
import com.healthware.utils.PasswordUtil;
import com.healthware.utils.RedisUtil;
import com.healthware.vo.DoctorLoginVO;
import com.healthware.vo.DoctorVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public PageResult<DoctorVO> listDoctors(int page, int size, Long deptId) {
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        if (deptId != null) {
            wrapper.eq(Doctor::getDepartmentId, deptId);
        }
        Page<Doctor> pageParam = new Page<>(page, size);
        Page<Doctor> result = doctorMapper.selectPage(pageParam, wrapper);
        List<DoctorVO> records = result.getRecords().stream().map(doctor -> {
            DoctorVO vo = doctorMapper.selectDetailById(doctor.getId());
            return vo;
        }).toList();
        return new PageResult<>(records, result.getTotal(), page, size);
    }

    @Override
    public DoctorVO getDetail(Long id) {
        DoctorVO vo = doctorMapper.selectDetailById(id);
        if (vo == null) {
            throw new BusinessException(ResultCode.DOCTOR_NOT_FOUND);
        }
        return vo;
    }

    @Override
    public List<DoctorVO> listByDepartment(Long deptId) {
        return doctorMapper.selectByDepartmentId(deptId);
    }

    @Override
    public void addDoctor(Doctor doctor) {
        doctor.setPassword(PasswordUtil.encrypt(doctor.getPassword()));
        doctor.setStatus(1);
        doctorMapper.insert(doctor);
    }

    @Override
    public void updateDoctor(Long id, Doctor doctor) {
        doctor.setId(id);
        if (doctor.getPassword() != null && !doctor.getPassword().isEmpty()) {
            doctor.setPassword(PasswordUtil.encrypt(doctor.getPassword()));
        } else {
            doctor.setPassword(null);
        }
        doctorMapper.updateById(doctor);
    }

    @Override
    public void deleteDoctor(Long id) {
        doctorMapper.deleteById(id);
    }

    @Override
    public DoctorLoginVO doctorLogin(LoginDTO dto) {
        Doctor doctor = doctorMapper.selectByUsername(dto.getUsername());
        if (doctor == null) {
            throw new BusinessException(ResultCode.DOCTOR_NOT_FOUND);
        }
        if (doctor.getStatus() == Constants.STATUS_DISABLED) {
            throw new BusinessException(ResultCode.DOCTOR_DISABLED);
        }
        if (!PasswordUtil.verify(dto.getPassword(), doctor.getPassword())) {
            throw new BusinessException(ResultCode.DOCTOR_PASSWORD_ERROR);
        }

        String token = jwtUtil.generateToken(doctor.getId(), doctor.getUsername(), "doctor");
        redisUtil.set(Constants.DOCTOR_TOKEN_PREFIX + doctor.getId(), token, 2, TimeUnit.HOURS);

        DoctorLoginVO vo = new DoctorLoginVO();
        vo.setToken(token);
        vo.setDoctorId(doctor.getId());
        vo.setUsername(doctor.getUsername());
        vo.setRealName(doctor.getName());
        return vo;
    }
}
