package com.healthware.service;

import com.healthware.common.PageResult;
import com.healthware.dto.PatientDTO;
import com.healthware.vo.PatientVO;

import java.util.List;

public interface PatientService {

    List<PatientVO> myPatients(Long userId);

    void addPatient(Long userId, PatientDTO dto);

    void updatePatient(Long id, PatientDTO dto);

    void deletePatient(Long id, Long userId);

    PageResult<PatientVO> listAll(int page, int size);
}
