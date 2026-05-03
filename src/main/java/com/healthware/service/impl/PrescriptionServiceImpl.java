package com.healthware.service.impl;

import com.healthware.dto.PrescriptionDTO;
import com.healthware.entity.Prescription;
import com.healthware.mapper.PrescriptionMapper;
import com.healthware.service.PrescriptionService;
import com.healthware.vo.PrescriptionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @Override
    public void createPrescription(PrescriptionDTO dto) {
        Prescription prescription = new Prescription();
        BeanUtils.copyProperties(dto, prescription);
        prescription.setStatus(0);
        prescriptionMapper.insert(prescription);
    }

    @Override
    public PrescriptionVO getDetail(Long id) {
        return prescriptionMapper.selectDetailById(id);
    }

    @Override
    public PrescriptionVO getByRegistrationId(Long registrationId) {
        return prescriptionMapper.selectByRegistrationId(registrationId);
    }

    @Override
    public void confirmDispense(Long id) {
        Prescription prescription = prescriptionMapper.selectById(id);
        if (prescription == null) return;
        prescription.setStatus(1);
        prescriptionMapper.updateById(prescription);
    }
}
