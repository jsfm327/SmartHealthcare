package com.healthware.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthware.dto.AppointmentDTO;
import com.healthware.dto.LoginDTO;
import com.healthware.dto.PatientDTO;
import com.healthware.dto.RegisterDTO;
import com.healthware.entity.Department;
import com.healthware.entity.Doctor;
import com.healthware.entity.Schedule;
import com.healthware.mapper.*;
import com.healthware.service.*;
import com.healthware.utils.JwtInterceptor;
import com.healthware.vo.LoginVO;
import com.healthware.vo.RegistrationVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 挂号完整链路测试
 * 测试流程：注册 → 登录 → 添加就诊人 → 查询科室/医生/排班 → 预约挂号 → 确认就诊
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("挂号完整链路测试")
class RegistrationFlowTest {

    @MockBean
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private ScheduleMapper scheduleMapper;

    private String userToken;
    private String testPhone;
    private Long patientId;
    private Long departmentId;
    private Long doctorId;
    private Long scheduleId;
    private Long registrationId;

    @Autowired
    private com.healthware.utils.JwtUtil jwtUtil;

    @BeforeEach
    void setUp() throws Exception {
        // 先注册并登录获取token
        registerAndLogin();
        // 配置mock拦截器：从token解析userId并设置到request属性
        org.mockito.Mockito.doAnswer(invocation -> {
            jakarta.servlet.http.HttpServletRequest request = invocation.getArgument(0);
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    String token = authHeader.substring(7);
                    Long userId = jwtUtil.getUserId(token);
                    request.setAttribute("userId", userId);
                    request.setAttribute("role", "user");
                } catch (Exception ignored) {}
            }
            return true;
        }).when(jwtInterceptor).preHandle(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any());
        // 准备其余测试数据
        prepareTestData();
    }

    private void registerAndLogin() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("testuser_" + System.currentTimeMillis());
        registerDTO.setPassword("password123");
        registerDTO.setRealName("测试用户");
        testPhone = "138" + String.format("%08d", System.currentTimeMillis() % 100000000);
        registerDTO.setPhone(testPhone);
        registerDTO.setGender(1);

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk());

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(registerDTO.getUsername());
        loginDTO.setPassword("password123");

        MvcResult loginResult = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        userToken = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .path("data").path("token").asText();
    }

    private void prepareTestData() throws Exception {
        // 1. 注册用户（已移至registerAndLogin）

        // 3. 添加就诊人
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setName("测试患者");
        patientDTO.setGender(1);
        patientDTO.setAge(30);
        patientDTO.setPhone(testPhone);
        patientDTO.setRelationship("本人");

        MvcResult patientResult = mockMvc.perform(post("/api/patient")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isOk())
                .andReturn();

        // 获取就诊人ID
        mockMvc.perform(get("/api/patient/my")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andDo(result -> {
                    String json = result.getResponse().getContentAsString();
                    patientId = objectMapper.readTree(json).path("data").get(0).path("id").asLong();
                });

        // 4. 创建科室
        Department dept = new Department();
        dept.setName("测试科室_" + System.currentTimeMillis());
        dept.setDescription("测试科室");
        dept.setLocation("门诊楼1层");
        departmentMapper.insert(dept);
        departmentId = dept.getId();

        // 5. 创建医生
        Doctor doctor = new Doctor();
        doctor.setName("测试医生");
        doctor.setDepartmentId(departmentId);
        doctor.setTitle("主任医师");
        doctor.setSpecialty("内科");
        doctor.setStatus(1);
        doctorMapper.insert(doctor);
        doctorId = doctor.getId();

        // 6. 创建排班
        Schedule schedule = new Schedule();
        schedule.setDoctorId(doctorId);
        schedule.setScheduleDate(LocalDate.now().plusDays(1));
        schedule.setTimeSlot(1);
        schedule.setStartTime(LocalTime.of(8, 0));
        schedule.setEndTime(LocalTime.of(12, 0));
        schedule.setCurrentAppointments(0);
        schedule.setMaxAppointments(15);
        schedule.setStatus(1);
        scheduleMapper.insert(schedule);
        scheduleId = schedule.getId();
    }

    @Test
    @Order(1)
    @DisplayName("完整挂号流程测试")
    void testCompleteRegistrationFlow() throws Exception {
        // 1. 查询科室列表
        mockMvc.perform(get("/api/department/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());

        // 2. 查询医生列表
        mockMvc.perform(get("/api/doctor/list")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 3. 查询排班列表
        mockMvc.perform(get("/api/schedule/list")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 4. 预约挂号
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPatientId(patientId);
        appointmentDTO.setDoctorId(doctorId);
        appointmentDTO.setScheduleId(scheduleId);
        appointmentDTO.setDepartmentId(departmentId);

        MvcResult appointmentResult = mockMvc.perform(post("/api/registration/appointment")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists())
                .andReturn();

        String appointmentJson = appointmentResult.getResponse().getContentAsString();
        registrationId = objectMapper.readTree(appointmentJson).path("data").path("id").asLong();
        assertNotNull(registrationId);

        // 5. 查询我的挂号
        mockMvc.perform(get("/api/registration/my")
                        .header("Authorization", "Bearer " + userToken)
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray());

        // 6. 查询挂号详情
        mockMvc.perform(get("/api/registration/" + registrationId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(registrationId));

        // 7. 确认就诊
        mockMvc.perform(put("/api/registration/" + registrationId + "/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 8. 验证挂号状态已更新
        mockMvc.perform(get("/api/registration/" + registrationId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(1)); // 1表示已就诊
    }

    @Test
    @Order(2)
    @DisplayName("取消挂号流程测试")
    void testCancelRegistrationFlow() throws Exception {
        // 1. 先预约挂号
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPatientId(patientId);
        appointmentDTO.setDoctorId(doctorId);
        appointmentDTO.setScheduleId(scheduleId);
        appointmentDTO.setDepartmentId(departmentId);

        MvcResult appointmentResult = mockMvc.perform(post("/api/registration/appointment")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String appointmentJson = appointmentResult.getResponse().getContentAsString();
        Long regId = objectMapper.readTree(appointmentJson).path("data").path("id").asLong();

        // 2. 取消挂号
        mockMvc.perform(put("/api/registration/" + regId + "/cancel")
                        .header("Authorization", "Bearer " + userToken)
                        .param("reason", "临时有事"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 3. 验证挂号状态已更新为已取消
        mockMvc.perform(get("/api/registration/" + regId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(2)); // 2表示已取消
    }
}
