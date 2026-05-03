package com.healthware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthware.common.PageResult;
import com.healthware.common.ResultCode;
import com.healthware.entity.Doctor;
import com.healthware.exception.BusinessException;
import com.healthware.mapper.DoctorMapper;
import com.healthware.service.DoctorService;
import com.healthware.vo.DoctorVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorMapper doctorMapper;

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
        doctor.setStatus(1);
        doctorMapper.insert(doctor);
    }

    @Override
    public void updateDoctor(Long id, Doctor doctor) {
        doctor.setId(id);
        doctorMapper.updateById(doctor);
    }

    @Override
    public void deleteDoctor(Long id) {
        doctorMapper.deleteById(id);
    }
}
