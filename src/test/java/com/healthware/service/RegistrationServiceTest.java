package com.healthware.service;

import com.healthware.common.Constants;
import com.healthware.common.PageResult;
import com.healthware.common.ResultCode;
import com.healthware.dto.AppointmentDTO;
import com.healthware.entity.Registration;
import com.healthware.entity.Schedule;
import com.healthware.exception.BusinessException;
import com.healthware.mapper.RegistrationMapper;
import com.healthware.mapper.ScheduleMapper;
import com.healthware.service.impl.RegistrationServiceImpl;
import com.healthware.vo.RegistrationVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private RegistrationMapper registrationMapper;

    @Mock
    private ScheduleMapper scheduleMapper;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    private Schedule testSchedule;
    private Registration testRegistration;

    @BeforeEach
    void setUp() {
        testSchedule = new Schedule();
        testSchedule.setId(1L);
        testSchedule.setDoctorId(1L);
        testSchedule.setScheduleDate(LocalDate.now().plusDays(1));
        testSchedule.setTimeSlot(1);
        testSchedule.setCurrentAppointments(5);
        testSchedule.setMaxAppointments(15);
        testSchedule.setStatus(1);

        testRegistration = new Registration();
        testRegistration.setId(1L);
        testRegistration.setUserId(1L);
        testRegistration.setPatientId(1L);
        testRegistration.setDoctorId(1L);
        testRegistration.setScheduleId(1L);
        testRegistration.setStatus(Constants.REG_STATUS_PENDING);
    }

    @Test
    void createAppointment_Success() {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setPatientId(1L);
        dto.setDoctorId(1L);
        dto.setScheduleId(1L);
        dto.setDepartmentId(1L);

        when(scheduleMapper.selectById(1L)).thenReturn(testSchedule);
        when(registrationMapper.countByPatientAndDate(anyLong(), anyString())).thenReturn(0);
        when(registrationMapper.selectMaxRegistrationNo(anyString())).thenReturn(null);
        when(registrationMapper.insert(any(Registration.class))).thenReturn(1);
        when(registrationMapper.selectDetailById(any())).thenReturn(new RegistrationVO());
        when(scheduleMapper.incrementCurrent(anyLong())).thenReturn(1);

        RegistrationVO result = registrationService.createAppointment(1L, dto);

        assertNotNull(result);
        verify(scheduleMapper).incrementCurrent(1L);
    }

    @Test
    void createAppointment_ScheduleFull() {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setPatientId(1L);
        dto.setDoctorId(1L);
        dto.setScheduleId(1L);
        dto.setDepartmentId(1L);

        testSchedule.setCurrentAppointments(15);
        when(scheduleMapper.selectById(1L)).thenReturn(testSchedule);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> registrationService.createAppointment(1L, dto));
        assertEquals(ResultCode.SCHEDULE_FULL.getCode(), exception.getCode());
    }

    @Test
    void createAppointment_DuplicateRegistration() {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setPatientId(1L);
        dto.setDoctorId(1L);
        dto.setScheduleId(1L);
        dto.setDepartmentId(1L);

        when(scheduleMapper.selectById(1L)).thenReturn(testSchedule);
        when(registrationMapper.countByPatientAndDate(anyLong(), anyString())).thenReturn(1);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> registrationService.createAppointment(1L, dto));
        assertEquals(ResultCode.DUPLICATE_REGISTRATION.getCode(), exception.getCode());
    }

    @Test
    void myRegistrations_Success() {
        List<Registration> registrations = Arrays.asList(testRegistration);
        when(registrationMapper.selectPage(any(), any())).thenReturn(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<Registration>(1, 10).setRecords(registrations));
        when(registrationMapper.selectDetailById(1L)).thenReturn(new RegistrationVO());

        PageResult<RegistrationVO> result = registrationService.myRegistrations(1L, 1, 10);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    @Test
    void getDetail_Success() {
        RegistrationVO vo = new RegistrationVO();
        vo.setId(1L);
        when(registrationMapper.selectDetailById(1L)).thenReturn(vo);

        RegistrationVO result = registrationService.getDetail(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getDetail_NotFound() {
        when(registrationMapper.selectDetailById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> registrationService.getDetail(999L));
    }

    @Test
    void cancelRegistration_Success() {
        when(registrationMapper.selectById(1L)).thenReturn(testRegistration);
        when(registrationMapper.updateById(any(Registration.class))).thenReturn(1);

        assertDoesNotThrow(() -> registrationService.cancelRegistration(1L, 1L, "临时有事"));
        verify(scheduleMapper).decrementCurrent(1L);
    }

    @Test
    void cancelRegistration_AlreadyCancelled() {
        testRegistration.setStatus(Constants.REG_STATUS_CANCELLED);
        when(registrationMapper.selectById(1L)).thenReturn(testRegistration);

        assertThrows(BusinessException.class,
                () -> registrationService.cancelRegistration(1L, 1L, "临时有事"));
    }

    @Test
    void confirmVisit_Success() {
        when(registrationMapper.selectById(1L)).thenReturn(testRegistration);
        when(registrationMapper.updateById(any(Registration.class))).thenReturn(1);

        assertDoesNotThrow(() -> registrationService.confirmVisit(1L));
        verify(registrationMapper).updateById(any(Registration.class));
    }
}
