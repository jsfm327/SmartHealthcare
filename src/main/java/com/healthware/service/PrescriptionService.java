package com.healthware.service;

import com.healthware.dto.PrescriptionDTO;
import com.healthware.vo.PrescriptionVO;

public interface PrescriptionService {

    void createPrescription(PrescriptionDTO dto);

    PrescriptionVO getDetail(Long id);

    PrescriptionVO getByRegistrationId(Long registrationId);

    void confirmDispense(Long id);

    void updatePrescription(Long id, PrescriptionDTO dto);
}
