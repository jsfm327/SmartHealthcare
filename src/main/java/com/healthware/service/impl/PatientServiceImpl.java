package com.healthware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthware.common.PageResult;
import com.healthware.common.ResultCode;
import com.healthware.dto.PatientDTO;
import com.healthware.entity.Patient;
import com.healthware.exception.BusinessException;
import com.healthware.mapper.PatientMapper;
import com.healthware.service.PatientService;
import com.healthware.vo.PatientVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientMapper patientMapper;

    @Override
    public List<PatientVO> myPatients(Long userId) {
        return patientMapper.selectByUserId(userId);
    }

    @Override
    public void addPatient(Long userId, PatientDTO dto) {
        Patient patient = new Patient();
        BeanUtils.copyProperties(dto, patient);
        patient.setUserId(userId);
        patientMapper.insert(patient);
    }

    @Override
    public void updatePatient(Long id, PatientDTO dto) {
        Patient patient = patientMapper.selectById(id);
        if (patient == null) {
            throw new BusinessException(ResultCode.PATIENT_NOT_FOUND);
        }
        BeanUtils.copyProperties(dto, patient, "id", "userId");
        patientMapper.updateById(patient);
    }

    @Override
    public void deletePatient(Long id, Long userId) {
        Patient patient = patientMapper.selectById(id);
        if (patient == null || !patient.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.PATIENT_NOT_FOUND);
        }
        patientMapper.deleteById(id);
    }

    @Override
    public PageResult<PatientVO> listAll(int page, int size) {
        Page<Patient> pageParam = new Page<>(page, size);
        Page<Patient> result = patientMapper.selectPage(pageParam, null);
        List<PatientVO> records = result.getRecords().stream().map(p -> {
            PatientVO vo = new PatientVO();
            BeanUtils.copyProperties(p, vo);
            return vo;
        }).toList();
        return new PageResult<>(records, result.getTotal(), page, size);
    }
}
