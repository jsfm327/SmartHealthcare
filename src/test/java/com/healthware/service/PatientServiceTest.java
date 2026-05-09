package com.healthware.service;

import com.healthware.common.PageResult;
import com.healthware.common.ResultCode;
import com.healthware.dto.PatientDTO;
import com.healthware.entity.Patient;
import com.healthware.exception.BusinessException;
import com.healthware.mapper.PatientMapper;
import com.healthware.service.impl.PatientServiceImpl;
import com.healthware.vo.PatientVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient testPatient;

    @BeforeEach
    void setUp() {
        testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setUserId(1L);
        testPatient.setName("张三");
        testPatient.setGender(1);
        testPatient.setAge(30);
        testPatient.setPhone("13800138000");
        testPatient.setRelationship("本人");
    }

    @Test
    void myPatients_Success() {
        List<PatientVO> patients = Arrays.asList(new PatientVO());
        when(patientMapper.selectByUserId(1L)).thenReturn(patients);

        List<PatientVO> result = patientService.myPatients(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void addPatient_Success() {
        PatientDTO dto = new PatientDTO();
        dto.setName("李四");
        dto.setGender(2);
        dto.setAge(28);
        dto.setRelationship("配偶");

        when(patientMapper.insert(any(Patient.class))).thenReturn(1);

        assertDoesNotThrow(() -> patientService.addPatient(1L, dto));
        verify(patientMapper).insert(any(Patient.class));
    }

    @Test
    void updatePatient_Success() {
        PatientDTO dto = new PatientDTO();
        dto.setName("更新姓名");

        when(patientMapper.selectById(1L)).thenReturn(testPatient);
        when(patientMapper.updateById(any(Patient.class))).thenReturn(1);

        assertDoesNotThrow(() -> patientService.updatePatient(1L, dto));
        verify(patientMapper).updateById(any(Patient.class));
    }

    @Test
    void updatePatient_NotFound() {
        PatientDTO dto = new PatientDTO();
        dto.setName("更新姓名");

        when(patientMapper.selectById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> patientService.updatePatient(999L, dto));
    }

    @Test
    void deletePatient_Success() {
        when(patientMapper.selectById(1L)).thenReturn(testPatient);
        when(patientMapper.deleteById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> patientService.deletePatient(1L, 1L));
        verify(patientMapper).deleteById(1L);
    }

    @Test
    void deletePatient_NotOwner() {
        when(patientMapper.selectById(1L)).thenReturn(testPatient);

        assertThrows(BusinessException.class, () -> patientService.deletePatient(1L, 2L));
    }

    @Test
    void listAll_Success() {
        List<Patient> patients = Arrays.asList(testPatient);
        when(patientMapper.selectPage(any(), any())).thenReturn(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<Patient>(1, 10).setRecords(patients));

        PageResult<PatientVO> result = patientService.listAll(1, 10);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }
}
