package com.healthware.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthware.entity.Patient;
import com.healthware.vo.PatientVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PatientMapper extends BaseMapper<Patient> {

    List<PatientVO> selectByUserId(@Param("userId") Long userId);
}
