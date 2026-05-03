package com.healthware.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthware.entity.MedicalRecord;
import com.healthware.vo.MedicalRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MedicalRecordMapper extends BaseMapper<MedicalRecord> {

    MedicalRecordVO selectDetailById(@Param("id") Long id);

    List<MedicalRecordVO> selectByPatientId(@Param("patientId") Long patientId);
}
