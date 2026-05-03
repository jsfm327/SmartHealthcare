package com.healthware.service;

import com.healthware.dto.MedicalRecordDTO;
import com.healthware.vo.MedicalRecordVO;

import java.util.List;

public interface MedicalRecordService {

    void createRecord(MedicalRecordDTO dto);

    MedicalRecordVO getDetail(Long id);

    List<MedicalRecordVO> listByPatient(Long patientId);

    void updateRecord(Long id, MedicalRecordDTO dto);
}
