package com.healthware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
        Page<Schedule> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        if (date != null) {
            wrapper.eq(Schedule::getScheduleDate, date);
        }
        wrapper.orderByDesc(Schedule::getScheduleDate);
        Page<Schedule> result = scheduleMapper.selectPage(pageParam, wrapper);
        List<ScheduleVO> records = scheduleMapper.selectWithDetail(date, deptId);
        return new PageResult<>(records, result.getTotal(), page, size);
    }

    @Override
    public List<ScheduleVO> listByDoctor(Long doctorId) {
        return scheduleMapper.selectByDoctorId(doctorId);
    }

    @Override
    public List<ScheduleVO> listByDate(String date) {
        return scheduleMapper.selectWithDetail(date, null);
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
