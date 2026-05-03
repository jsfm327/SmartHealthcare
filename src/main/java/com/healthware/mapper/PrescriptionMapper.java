package com.healthware.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthware.entity.Prescription;
import com.healthware.vo.PrescriptionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PrescriptionMapper extends BaseMapper<Prescription> {

    PrescriptionVO selectDetailById(@Param("id") Long id);

    PrescriptionVO selectByRegistrationId(@Param("registrationId") Long registrationId);
}
