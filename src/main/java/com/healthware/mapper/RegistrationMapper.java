package com.healthware.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthware.entity.Registration;
import com.healthware.vo.RegistrationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RegistrationMapper extends BaseMapper<Registration> {

    RegistrationVO selectDetailById(@Param("id") Long id);

    List<RegistrationVO> selectByUserId(@Param("userId") Long userId);

    int countByPatientAndDate(@Param("patientId") Long patientId, @Param("date") String date);

    String selectMaxRegistrationNo(@Param("datePrefix") String datePrefix);

    int expirePastRegistrations(@Param("today") String today);

    List<RegistrationVO> selectByDoctorId(@Param("doctorId") Long doctorId);
}
