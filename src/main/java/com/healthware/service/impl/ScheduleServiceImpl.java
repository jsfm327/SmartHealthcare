package com.healthware.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthware.common.PageResult;
import com.healthware.dto.ScheduleDTO;
import com.healthware.entity.Schedule;
import com.healthware.mapper.ScheduleMapper;
import com.healthware.service.ScheduleService;
import com.healthware.vo.ScheduleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Override
    public PageResult<ScheduleVO> listSchedules(int page, int size, String date, Long deptId) {
        Page<ScheduleVO> pageParam = new Page<>(page, size);
        Page<ScheduleVO> result = scheduleMapper.selectWithDetail(pageParam, date, deptId);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }

    @Override
    public List<ScheduleVO> listByDoctor(Long doctorId) {
        return scheduleMapper.selectByDoctorId(doctorId);
    }

    @Override
    public ScheduleVO getDetail(Long id) {
        return scheduleMapper.selectByIdWithDetail(id);
    }

    @Override
    public List<ScheduleVO> listByDate(String date) {
        return scheduleMapper.selectWithDetail(new Page<>(), date, null).getRecords();
    }

    @Override
    public void addSchedule(ScheduleDTO dto) {
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(dto, schedule);
        schedule.setCurrentAppointments(0);
        schedule.setStatus(1);
        scheduleMapper.insert(schedule);
    }

    @Override
    public void updateSchedule(Long id, ScheduleDTO dto) {
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(dto, schedule);
        schedule.setId(id);
        scheduleMapper.updateById(schedule);
    }

    @Override
    public void deleteSchedule(Long id) {
        scheduleMapper.deleteById(id);
    }

    @Override
    public void incrementAppointments(Long scheduleId) {
        scheduleMapper.incrementCurrent(scheduleId);
    }

    @Override
    public void decrementAppointments(Long scheduleId) {
        scheduleMapper.decrementCurrent(scheduleId);
    }
}
