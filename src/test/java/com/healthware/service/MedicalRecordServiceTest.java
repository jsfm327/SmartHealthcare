package com.healthware.service;

import com.healthware.dto.MedicalRecordDTO;
import com.healthware.entity.MedicalRecord;
import com.healthware.mapper.MedicalRecordMapper;
import com.healthware.service.impl.MedicalRecordServiceImpl;
import com.healthware.vo.MedicalRecordVO;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordMapper medicalRecordMapper;

    @InjectMocks
    private MedicalRecordServiceImpl medicalRecordService;

    private MedicalRecord testRecord;

    @BeforeEach
    void setUp() {
        testRecord = new MedicalRecord();
        testRecord.setId(1L);
        testRecord.setPatientId(1L);
        testRecord.setDoctorId(1L);
        testRecord.setRegistrationId(1L);
        testRecord.setChiefComplaint("发热3天，咳嗽");
        testRecord.setDiagnosis("上呼吸道感染");
        testRecord.setVisitDate(LocalDate.now());
    }

    @Test
    void createRecord_Success() {
        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setPatientId(1L);
        dto.setDoctorId(1L);
        dto.setRegistrationId(1L);
        dto.setChiefComplaint("发热3天，咳嗽");
        dto.setDiagnosis("上呼吸道感染");
        dto.setVisitDate(LocalDate.now());

        when(medicalRecordMapper.insert(any(MedicalRecord.class))).thenReturn(1);

        assertDoesNotThrow(() -> medicalRecordService.createRecord(dto));
        verify(medicalRecordMapper).insert(any(MedicalRecord.class));
    }

    @Test
    void getDetail_Success() {
        MedicalRecordVO vo = new MedicalRecordVO();
        vo.setId(1L);
        vo.setChiefComplaint("发热3天，咳嗽");
        vo.setDiagnosis("上呼吸道感染");
        when(medicalRecordMapper.selectDetailById(1L)).thenReturn(vo);

        MedicalRecordVO result = medicalRecordService.getDetail(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("上呼吸道感染", result.getDiagnosis());
    }

    @Test
    void getDetail_NotFound() {
        when(medicalRecordMapper.selectDetailById(999L)).thenReturn(null);

        MedicalRecordVO result = medicalRecordService.getDetail(999L);

        assertNull(result);
    }

    @Test
    void listByPatient_Success() {
        List<MedicalRecordVO> records = Arrays.asList(new MedicalRecordVO());
        when(medicalRecordMapper.selectByPatientId(1L)).thenReturn(records);

        List<MedicalRecordVO> result = medicalRecordService.listByPatient(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void updateRecord_Success() {
        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setPatientId(1L);
        dto.setDoctorId(1L);
        dto.setChiefComplaint("更新后的主诉");
        dto.setDiagnosis("更新后的诊断");
        dto.setVisitDate(LocalDate.now());

        when(medicalRecordMapper.updateById(any(MedicalRecord.class))).thenReturn(1);

        assertDoesNotThrow(() -> medicalRecordService.updateRecord(1L, dto));
        verify(medicalRecordMapper).updateById(any(MedicalRecord.class));
    }
}
