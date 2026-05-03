package com.healthware.service;

import com.healthware.common.PageResult;
import com.healthware.entity.Department;
import com.healthware.vo.DepartmentVO;

import java.util.List;

public interface DepartmentService {

    List<DepartmentVO> listAll();

    PageResult<DepartmentVO> listPage(int page, int size);

    DepartmentVO getDetail(Long id);

    void addDepartment(Department dept);

    void updateDepartment(Long id, Department dept);

    void deleteDepartment(Long id);
}
