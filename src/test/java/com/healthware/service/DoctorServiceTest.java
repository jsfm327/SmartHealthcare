package com.healthware.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.healthware.common.Constants;
import com.healthware.common.PageResult;
import com.healthware.entity.Doctor;
import com.healthware.exception.BusinessException;
import com.healthware.mapper.DoctorMapper;
import com.healthware.service.impl.DoctorServiceImpl;
import com.healthware.vo.DoctorVO;
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
class DoctorServiceTest {

    @Mock
    private DoctorMapper doctorMapper;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private Doctor testDoctor;

    @BeforeEach
    void setUp() {
        testDoctor = new Doctor();
        testDoctor.setId(1L);
        testDoctor.setName("李医生");
        testDoctor.setDepartmentId(1L);
        testDoctor.setTitle("主任医师");
        testDoctor.setSpecialty("心血管疾病");
        testDoctor.setStatus(Constants.STATUS_NORMAL);
    }

    @Test
    void listDoctors_Success() {
        List<Doctor> doctors = Arrays.asList(testDoctor);
        when(doctorMapper.selectPage(any(), any())).thenReturn(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<Doctor>(1, 10).setRecords(doctors));
        when(doctorMapper.selectDetailById(anyLong())).thenReturn(new DoctorVO());

        PageResult<DoctorVO> result = doctorService.listDoctors(1, 10, null);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    @Test
    void listDoctors_ByDepartment() {
        List<Doctor> doctors = Arrays.asList(testDoctor);
        when(doctorMapper.selectPage(any(), any())).thenReturn(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<Doctor>(1, 10).setRecords(doctors));
        when(doctorMapper.selectDetailById(anyLong())).thenReturn(new DoctorVO());

        PageResult<DoctorVO> result = doctorService.listDoctors(1, 10, 1L);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    @Test
    void getDetail_Success() {
        DoctorVO vo = new DoctorVO();
        vo.setId(1L);
        vo.setName("李医生");
        when(doctorMapper.selectDetailById(1L)).thenReturn(vo);

        DoctorVO result = doctorService.getDetail(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("李医生", result.getName());
    }

    @Test
    void getDetail_NotFound() {
        when(doctorMapper.selectDetailById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> doctorService.getDetail(999L));
    }

    @Test
    void listByDepartment_Success() {
        List<DoctorVO> doctors = Arrays.asList(new DoctorVO());
        when(doctorMapper.selectByDepartmentId(1L)).thenReturn(doctors);

        List<DoctorVO> result = doctorService.listByDepartment(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void addDoctor_Success() {
        when(doctorMapper.insert(any(Doctor.class))).thenReturn(1);

        assertDoesNotThrow(() -> doctorService.addDoctor(testDoctor));
        verify(doctorMapper).insert(any(Doctor.class));
    }

    @Test
    void updateDoctor_Success() {
        when(doctorMapper.updateById(any(Doctor.class))).thenReturn(1);

        assertDoesNotThrow(() -> doctorService.updateDoctor(1L, testDoctor));
        verify(doctorMapper).updateById(any(Doctor.class));
    }

    @Test
    void deleteDoctor_Success() {
        when(doctorMapper.deleteById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> doctorService.deleteDoctor(1L));
        verify(doctorMapper).deleteById(1L);
    }
}
