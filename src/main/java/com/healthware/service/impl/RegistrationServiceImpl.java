package com.healthware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthware.common.Constants;
import com.healthware.common.PageResult;
import com.healthware.common.ResultCode;
import com.healthware.dto.AppointmentDTO;
import com.healthware.entity.Registration;
import com.healthware.entity.Schedule;
import com.healthware.exception.BusinessException;
import com.healthware.mapper.RegistrationMapper;
import com.healthware.mapper.ScheduleMapper;
import com.healthware.service.RegistrationService;
import com.healthware.vo.RegistrationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private RegistrationMapper registrationMapper;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Override
    @Transactional
    public RegistrationVO createAppointment(Long userId, AppointmentDTO dto) {
        Schedule schedule = scheduleMapper.selectById(dto.getScheduleId());
        if (schedule == null || schedule.getStatus() != 1) {
            throw new BusinessException(ResultCode.SCHEDULE_NOT_AVAILABLE);
        }
        if (schedule.getCurrentAppointments() >= schedule.getMaxAppointments()) {
            throw new BusinessException(ResultCode.SCHEDULE_FULL);
        }

        String dateStr = schedule.getScheduleDate().format(DateTimeFormatter.ISO_DATE);
        int count = registrationMapper.countByPatientAndDate(dto.getPatientId(), dateStr);
        if (count > 0) {
            throw new BusinessException(ResultCode.DUPLICATE_REGISTRATION);
        }

        String datePrefix = "REG" + schedule.getScheduleDate().format(DateTimeFormatter.BASIC_ISO_DATE);
        String maxNo = registrationMapper.selectMaxRegistrationNo(datePrefix + "%");
        int seq = 1;
        if (maxNo != null) {
            seq = Integer.parseInt(maxNo.substring(datePrefix.length())) + 1;
        }
        String registrationNo = datePrefix + String.format("%04d", seq);

        Registration registration = new Registration();
        registration.setRegistrationNo(registrationNo);
        registration.setUserId(userId);
        registration.setPatientId(dto.getPatientId());
        registration.setDoctorId(dto.getDoctorId());
        registration.setDepartmentId(dto.getDepartmentId());
        registration.setRoomId(schedule.getRoomId());
        registration.setScheduleId(dto.getScheduleId());
        registration.setRegistrationDate(schedule.getScheduleDate());
        registration.setTimeSlot(schedule.getTimeSlot());
        registration.setQueueNumber(schedule.getCurrentAppointments() + 1);
        registration.setStatus(Constants.REG_STATUS_PENDING);
        registrationMapper.insert(registration);

        scheduleMapper.incrementCurrent(schedule.getId());

        // 检查是否已满
        Schedule updatedSchedule = scheduleMapper.selectById(schedule.getId());
        if (updatedSchedule.getCurrentAppointments() >= updatedSchedule.getMaxAppointments()) {
            updatedSchedule.setStatus(2); // 已满
            scheduleMapper.updateById(updatedSchedule);
        }

        return registrationMapper.selectDetailById(registration.getId());
    }

    @Override
    public PageResult<RegistrationVO> myRegistrations(Long userId, int page, int size) {
        Page<Registration> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Registration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Registration::getUserId, userId).orderByDesc(Registration::getCreateTime);
        Page<Registration> result = registrationMapper.selectPage(pageParam, wrapper);
        List<RegistrationVO> records = result.getRecords().stream()
                .map(r -> registrationMapper.selectDetailById(r.getId()))
                .toList();
        return new PageResult<>(records, result.getTotal(), page, size);
    }

    @Override
    public RegistrationVO getDetail(Long id) {
        RegistrationVO vo = registrationMapper.selectDetailById(id);
        if (vo == null) {
            throw new BusinessException(ResultCode.REGISTRATION_NOT_FOUND);
        }
        return vo;
    }

    @Override
    @Transactional
    public void cancelRegistration(Long id, Long userId, String reason) {
        Registration reg = registrationMapper.selectById(id);
        if (reg == null) {
            throw new BusinessException(ResultCode.REGISTRATION_NOT_FOUND);
        }
        if (reg.getStatus() != Constants.REG_STATUS_PENDING) {
            throw new BusinessException(ResultCode.REGISTRATION_CANCELLED);
        }
        reg.setStatus(Constants.REG_STATUS_CANCELLED);
        reg.setCancelReason(reason);
        registrationMapper.updateById(reg);

        scheduleMapper.decrementCurrent(reg.getScheduleId());
    }

    @Override
    public PageResult<RegistrationVO> listAll(int page, int size, String date, Long deptId) {
        Page<Registration> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Registration> wrapper = new LambdaQueryWrapper<>();
        if (date != null) {
            wrapper.eq(Registration::getRegistrationDate, date);
        }
        if (deptId != null) {
            wrapper.eq(Registration::getDepartmentId, deptId);
        }
        wrapper.orderByDesc(Registration::getCreateTime);
        Page<Registration> result = registrationMapper.selectPage(pageParam, wrapper);
        List<RegistrationVO> records = result.getRecords().stream()
                .map(r -> registrationMapper.selectDetailById(r.getId()))
                .toList();
        return new PageResult<>(records, result.getTotal(), page, size);
    }

    @Override
    public void confirmVisit(Long id) {
        Registration reg = registrationMapper.selectById(id);
        if (reg == null) {
            throw new BusinessException(ResultCode.REGISTRATION_NOT_FOUND);
        }
        reg.setStatus(Constants.REG_STATUS_VISITED);
        registrationMapper.updateById(reg);
    }
}
