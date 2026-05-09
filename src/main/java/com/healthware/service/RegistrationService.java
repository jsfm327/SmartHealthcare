package com.healthware.service;

import com.healthware.common.PageResult;
import com.healthware.dto.AppointmentDTO;
import com.healthware.vo.RegistrationVO;

import java.util.List;

public interface RegistrationService {

    RegistrationVO createAppointment(Long userId, AppointmentDTO dto);

    PageResult<RegistrationVO> myRegistrations(Long userId, int page, int size);

    RegistrationVO getDetail(Long id);

    void cancelRegistration(Long id, Long userId, String reason);

    PageResult<RegistrationVO> listAll(int page, int size, String date, Long deptId);

    void confirmVisit(Long id);

    List<RegistrationVO> listByDoctorId(Long doctorId);
}
