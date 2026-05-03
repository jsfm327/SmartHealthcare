package com.healthware.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthware.entity.Schedule;
import com.healthware.vo.ScheduleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScheduleMapper extends BaseMapper<Schedule> {

    List<ScheduleVO> selectWithDetail(@Param("date") String date, @Param("deptId") Long deptId);

    List<ScheduleVO> selectByDoctorId(@Param("doctorId") Long doctorId);

    int incrementCurrent(@Param("id") Long id);

    int decrementCurrent(@Param("id") Long id);
}
