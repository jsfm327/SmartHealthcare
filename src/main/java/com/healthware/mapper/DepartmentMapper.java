package com.healthware.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthware.entity.Department;
import com.healthware.vo.DepartmentVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {

    List<DepartmentVO> selectWithDoctorCount();
}
