package com.healthware.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthware.common.PageResult;
import com.healthware.dto.ScheduleDTO;
import com.healthware.entity.Schedule;
import com.healthware.mapper.ScheduleMapper;
import com.healthware.service.impl.ScheduleServiceImpl;
import com.healthware.vo.ScheduleVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleMapper scheduleMapper;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    private Schedule testSchedule;

    @BeforeEach
    void setUp() {
        testSchedule = new Schedule();
        testSchedule.setId(1L);
        testSchedule.setDoctorId(1L);
        testSchedule.setScheduleDate(LocalDate.now().plusDays(1));
        testSchedule.setTimeSlot(1);
        testSchedule.setStartTime(LocalTime.of(8, 0));
        testSchedule.setEndTime(LocalTime.of(12, 0));
        testSchedule.setCurrentAppointments(0);
        testSchedule.setMaxAppointments(15);
        testSchedule.setStatus(1);
    }

    @Test
    void listSchedules_Success() {
        Page<ScheduleVO> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(new ScheduleVO()));
        when(scheduleMapper.selectWithDetail(any(), any(), any())).thenReturn(page);

        PageResult<ScheduleVO> result = scheduleService.listSchedules(1, 10, null, null);

        assertNotNull(result);
    }

    @Test
    void listByDoctor_Success() {
        List<ScheduleVO> schedules = Arrays.asList(new ScheduleVO());
        when(scheduleMapper.selectByDoctorId(1L)).thenReturn(schedules);

        List<ScheduleVO> result = scheduleService.listByDoctor(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void listByDate_Success() {
        Page<ScheduleVO> page = new Page<>();
        page.setRecords(Arrays.asList(new ScheduleVO()));
        when(scheduleMapper.selectWithDetail(any(), anyString(), any())).thenReturn(page);

        List<ScheduleVO> result = scheduleService.listByDate("2026-05-10");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void addSchedule_Success() {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setDoctorId(1L);
        dto.setScheduleDate(LocalDate.now().plusDays(1));
        dto.setTimeSlot(1);
        dto.setStartTime(LocalTime.of(8, 0));
        dto.setEndTime(LocalTime.of(12, 0));
        dto.setMaxAppointments(15);

        when(scheduleMapper.insert(any(Schedule.class))).thenReturn(1);

        assertDoesNotThrow(() -> scheduleService.addSchedule(dto));
        verify(scheduleMapper).insert(any(Schedule.class));
    }

    @Test
    void updateSchedule_Success() {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setDoctorId(1L);
        dto.setScheduleDate(LocalDate.now().plusDays(1));
        dto.setTimeSlot(1);
        dto.setStartTime(LocalTime.of(8, 0));
        dto.setEndTime(LocalTime.of(12, 0));

        when(scheduleMapper.updateById(any(Schedule.class))).thenReturn(1);

        assertDoesNotThrow(() -> scheduleService.updateSchedule(1L, dto));
        verify(scheduleMapper).updateById(any(Schedule.class));
    }

    @Test
    void deleteSchedule_Success() {
        when(scheduleMapper.deleteById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> scheduleService.deleteSchedule(1L));
        verify(scheduleMapper).deleteById(1L);
    }

    @Test
    void incrementAppointments_Success() {
        when(scheduleMapper.incrementCurrent(1L)).thenReturn(1);

        assertDoesNotThrow(() -> scheduleService.incrementAppointments(1L));
        verify(scheduleMapper).incrementCurrent(1L);
    }

    @Test
    void decrementAppointments_Success() {
        when(scheduleMapper.decrementCurrent(1L)).thenReturn(1);

        assertDoesNotThrow(() -> scheduleService.decrementAppointments(1L));
        verify(scheduleMapper).decrementCurrent(1L);
    }
}
