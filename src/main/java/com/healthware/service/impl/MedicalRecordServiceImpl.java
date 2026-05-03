package com.healthware.service.impl;

import com.healthware.dto.MedicalRecordDTO;
import com.healthware.entity.MedicalRecord;
import com.healthware.mapper.MedicalRecordMapper;
import com.healthware.service.MedicalRecordService;
import com.healthware.vo.MedicalRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalRecordServiceImpl implements MedicalRecordService {

    @Autowired
    private MedicalRecordMapper medicalRecordMapper;

    @Override
    public void createRecord(MedicalRecordDTO dto) {
        MedicalRecord record = new MedicalRecord();
        BeanUtils.copyProperties(dto, record);
        medicalRecordMapper.insert(record);
    }

    @Override
    public MedicalRecordVO getDetail(Long id) {
        return medicalRecordMapper.selectDetailById(id);
    }

    @Override
    public List<MedicalRecordVO> listByPatient(Long patientId) {
        return medicalRecordMapper.selectByPatientId(patientId);
    }

    @Override
    public void updateRecord(Long id, MedicalRecordDTO dto) {
        MedicalRecord record = new MedicalRecord();
        BeanUtils.copyProperties(dto, record);
        record.setId(id);
        medicalRecordMapper.updateById(record);
    }
}
