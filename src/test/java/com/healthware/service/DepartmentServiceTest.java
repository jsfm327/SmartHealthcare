package com.healthware.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.healthware.common.Constants;
import com.healthware.common.PageResult;
import com.healthware.entity.Department;
import com.healthware.exception.BusinessException;
import com.healthware.mapper.DepartmentMapper;
import com.healthware.service.impl.DepartmentServiceImpl;
import com.healthware.vo.DepartmentVO;
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
class DepartmentServiceTest {

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department testDept;

    @BeforeEach
    void setUp() {
        testDept = new Department();
        testDept.setId(1L);
        testDept.setName("内科");
        testDept.setDescription("内科疾病诊治");
        testDept.setLocation("门诊楼2层");
        testDept.setStatus(Constants.STATUS_NORMAL);
    }

    @Test
    void listAll_Success() {
        List<Department> depts = Arrays.asList(testDept);
        when(departmentMapper.selectWithDoctorCount()).thenReturn(Arrays.asList(new DepartmentVO()));

        List<DepartmentVO> result = departmentService.listAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void listPage_Success() {
        List<Department> depts = Arrays.asList(testDept);
        when(departmentMapper.selectPage(any(), any())).thenReturn(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<Department>(1, 10).setRecords(depts));

        PageResult<DepartmentVO> result = departmentService.listPage(1, 10);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    @Test
    void getDetail_Success() {
        when(departmentMapper.selectById(1L)).thenReturn(testDept);

        DepartmentVO result = departmentService.getDetail(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("内科", result.getName());
    }

    @Test
    void getDetail_NotFound() {
        when(departmentMapper.selectById(999L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> departmentService.getDetail(999L));
    }

    @Test
    void addDepartment_Success() {
        when(departmentMapper.insert(any(Department.class))).thenReturn(1);

        assertDoesNotThrow(() -> departmentService.addDepartment(testDept));
        verify(departmentMapper).insert(any(Department.class));
    }

    @Test
    void updateDepartment_Success() {
        when(departmentMapper.updateById(any(Department.class))).thenReturn(1);

        assertDoesNotThrow(() -> departmentService.updateDepartment(1L, testDept));
        verify(departmentMapper).updateById(any(Department.class));
    }

    @Test
    void deleteDepartment_Success() {
        when(departmentMapper.deleteById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> departmentService.deleteDepartment(1L));
        verify(departmentMapper).deleteById(1L);
    }
}
