package com.healthware.service;

import com.healthware.common.PageResult;
import com.healthware.dto.ScheduleDTO;
import com.healthware.vo.ScheduleVO;

import java.util.List;

public interface ScheduleService {

    PageResult<ScheduleVO> listSchedules(int page, int size, String date, Long deptId);

    List<ScheduleVO> listByDoctor(Long doctorId);

    List<ScheduleVO> listByDate(String date);

    void addSchedule(ScheduleDTO dto);

    void updateSchedule(Long id, ScheduleDTO dto);

    void deleteSchedule(Long id);

    void incrementAppointments(Long scheduleId);

    void decrementAppointments(Long scheduleId);
}
