package com.healthware.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthware.entity.Doctor;
import com.healthware.vo.DoctorVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {

    DoctorVO selectDetailById(@Param("id") Long id);

    List<DoctorVO> selectByDepartmentId(@Param("deptId") Long deptId);

    Doctor selectByUsername(@Param("username") String username);
}
