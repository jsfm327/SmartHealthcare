package com.healthware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthware.common.PageResult;
import com.healthware.common.ResultCode;
import com.healthware.entity.Department;
import com.healthware.exception.BusinessException;
import com.healthware.entity.Doctor;
import com.healthware.mapper.DepartmentMapper;
import com.healthware.mapper.DoctorMapper;
import com.healthware.service.DepartmentService;
import com.healthware.vo.DepartmentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Override
    public List<DepartmentVO> listAll() {
        return departmentMapper.selectWithDoctorCount();
    }

    @Override
    public PageResult<DepartmentVO> listPage(int page, int size) {
        Page<Department> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Department::getSortOrder);
        Page<Department> result = departmentMapper.selectPage(pageParam, wrapper);
        // 简单返回，doctorCount 可后续扩展
        List<DepartmentVO> records = result.getRecords().stream().map(dept -> {
            DepartmentVO vo = new DepartmentVO();
            vo.setId(dept.getId());
            vo.setName(dept.getName());
            vo.setDescription(dept.getDescription());
            vo.setLocation(dept.getLocation());
            vo.setPhone(dept.getPhone());
            vo.setStatus(dept.getStatus());
            vo.setSortOrder(dept.getSortOrder());
            return vo;
        }).toList();
        return new PageResult<>(records, result.getTotal(), page, size);
    }

    @Override
    public DepartmentVO getDetail(Long id) {
        Department dept = departmentMapper.selectById(id);
        if (dept == null) {
            throw new BusinessException(ResultCode.DEPARTMENT_NOT_FOUND);
        }
        DepartmentVO vo = new DepartmentVO();
        vo.setId(dept.getId());
        vo.setName(dept.getName());
        vo.setDescription(dept.getDescription());
        vo.setLocation(dept.getLocation());
        vo.setPhone(dept.getPhone());
        vo.setStatus(dept.getStatus());
        vo.setSortOrder(dept.getSortOrder());
        return vo;
    }

    @Override
    public void addDepartment(Department dept) {
        dept.setStatus(1);
        departmentMapper.insert(dept);
    }

    @Override
    public void updateDepartment(Long id, Department dept) {
        dept.setId(id);
        departmentMapper.updateById(dept);
    }

    @Override
    public void deleteDepartment(Long id) {
        Long doctorCount = doctorMapper.selectCount(
                new LambdaQueryWrapper<Doctor>().eq(Doctor::getDepartmentId, id));
        if (doctorCount > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(),
                    "该科室下有 " + doctorCount + " 名医生，请先删除或转移医生后再删除科室");
        }
        try {
            departmentMapper.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "该科室存在关联数据，无法删除");
        }
    }
}
