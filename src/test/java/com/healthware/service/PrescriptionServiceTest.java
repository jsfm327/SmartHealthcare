package com.healthware.service;

import com.healthware.dto.PrescriptionDTO;
import com.healthware.entity.Prescription;
import com.healthware.mapper.PrescriptionMapper;
import com.healthware.service.impl.PrescriptionServiceImpl;
import com.healthware.vo.PrescriptionVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceTest {

    @Mock
    private PrescriptionMapper prescriptionMapper;

    @InjectMocks
    private PrescriptionServiceImpl prescriptionService;

    private Prescription testPrescription;

    @BeforeEach
    void setUp() {
        testPrescription = new Prescription();
        testPrescription.setId(1L);
        testPrescription.setRegistrationId(1L);
        testPrescription.setDoctorId(1L);
        testPrescription.setPatientId(1L);
        testPrescription.setDiagnosis("上呼吸道感染");
        testPrescription.setMedicineList("阿莫西林胶囊 x2");
        testPrescription.setTotalAmount(new BigDecimal("128.50"));
        testPrescription.setStatus(0);
    }

    @Test
    void createPrescription_Success() {
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setRegistrationId(1L);
        dto.setDoctorId(1L);
        dto.setPatientId(1L);
        dto.setDiagnosis("上呼吸道感染");
        dto.setMedicineList("阿莫西林胶囊 x2");
        dto.setTotalAmount(new BigDecimal("128.50"));

        when(prescriptionMapper.insert(any(Prescription.class))).thenReturn(1);

        assertDoesNotThrow(() -> prescriptionService.createPrescription(dto));
        verify(prescriptionMapper).insert(any(Prescription.class));
    }

    @Test
    void getDetail_Success() {
        PrescriptionVO vo = new PrescriptionVO();
        vo.setId(1L);
        vo.setDiagnosis("上呼吸道感染");
        when(prescriptionMapper.selectDetailById(1L)).thenReturn(vo);

        PrescriptionVO result = prescriptionService.getDetail(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("上呼吸道感染", result.getDiagnosis());
    }

    @Test
    void getDetail_NotFound() {
        when(prescriptionMapper.selectDetailById(999L)).thenReturn(null);

        PrescriptionVO result = prescriptionService.getDetail(999L);

        assertNull(result);
    }

    @Test
    void getByRegistrationId_Success() {
        PrescriptionVO vo = new PrescriptionVO();
        vo.setId(1L);
        vo.setRegistrationId(1L);
        when(prescriptionMapper.selectByRegistrationId(1L)).thenReturn(vo);

        PrescriptionVO result = prescriptionService.getByRegistrationId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getRegistrationId());
    }

    @Test
    void confirmDispense_Success() {
        when(prescriptionMapper.selectById(1L)).thenReturn(testPrescription);
        when(prescriptionMapper.updateById(testPrescription)).thenReturn(1);

        assertDoesNotThrow(() -> prescriptionService.confirmDispense(1L));
        assertEquals(1, testPrescription.getStatus());
        verify(prescriptionMapper).updateById(testPrescription);
    }

    @Test
    void confirmDispense_NotFound() {
        when(prescriptionMapper.selectById(999L)).thenReturn(null);

        assertDoesNotThrow(() -> prescriptionService.confirmDispense(999L));
        verify(prescriptionMapper, never()).updateById(any(Prescription.class));
    }
}
