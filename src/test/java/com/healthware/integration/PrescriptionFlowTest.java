package com.healthware.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthware.dto.*;
import com.healthware.entity.*;
import com.healthware.mapper.*;
import com.healthware.utils.JwtInterceptor;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 处方完整链路测试
 * 测试流程：挂号 → 开具处方 → 查询处方 → 确认发药
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("处方完整链路测试")
class PrescriptionFlowTest {

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

    @Autowired
    private com.healthware.utils.JwtUtil jwtUtil;

    private String userToken;
    private String testPhone;
    private Long patientId;
    private Long doctorId;
    private Long registrationId;

    @BeforeEach
    void setUp() throws Exception {
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
        prepareTestData();
    }

    private void registerAndLogin() throws Exception {
        String username = "testuser_" + System.currentTimeMillis();
        testPhone = "138" + String.format("%08d", System.currentTimeMillis() % 100000000);

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername(username);
        registerDTO.setPassword("password123");
        registerDTO.setRealName("测试用户");
        registerDTO.setPhone(testPhone);
        registerDTO.setGender(1);

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk());

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(username);
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
        // 添加就诊人
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setName("测试患者");
        patientDTO.setGender(1);
        patientDTO.setAge(30);
        patientDTO.setPhone(testPhone);
        patientDTO.setRelationship("本人");

        mockMvc.perform(post("/api/patient")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/patient/my")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String json = result.getResponse().getContentAsString();
                    patientId = objectMapper.readTree(json).path("data").get(0).path("id").asLong();
                });

        // 创建测试数据
        Department dept = new Department();
        dept.setName("测试科室_" + System.currentTimeMillis());
        dept.setDescription("测试");
        dept.setLocation("1层");
        departmentMapper.insert(dept);

        Doctor doctor = new Doctor();
        doctor.setName("测试医生");
        doctor.setDepartmentId(dept.getId());
        doctor.setTitle("主任医师");
        doctor.setSpecialty("内科");
        doctor.setStatus(1);
        doctorMapper.insert(doctor);
        doctorId = doctor.getId();

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

        // 预约挂号
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setPatientId(patientId);
        appointmentDTO.setDoctorId(doctorId);
        appointmentDTO.setScheduleId(schedule.getId());
        appointmentDTO.setDepartmentId(dept.getId());

        MvcResult regResult = mockMvc.perform(post("/api/registration/appointment")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentDTO)))
                .andExpect(status().isOk())
                .andReturn();

        registrationId = objectMapper.readTree(regResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();
    }

    @Test
    @Order(1)
    @DisplayName("开具处方流程测试")
    void testCreatePrescriptionFlow() throws Exception {
        // 1. 开具处方
        PrescriptionDTO prescriptionDTO = new PrescriptionDTO();
        prescriptionDTO.setRegistrationId(registrationId);
        prescriptionDTO.setDoctorId(doctorId);
        prescriptionDTO.setPatientId(patientId);
        prescriptionDTO.setMedicineList("阿莫西林胶囊 x2, 布洛芬片 x1");
        prescriptionDTO.setTotalAmount(new BigDecimal("128.50"));
        prescriptionDTO.setNotes("饭后服用");

        mockMvc.perform(post("/api/prescription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(prescriptionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 2. 按挂号ID查询处方
        MvcResult queryResult = mockMvc.perform(get("/api/prescription/registration/" + registrationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists())
                .andReturn();

        Long prescriptionId = objectMapper.readTree(queryResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        // 3. 确认发药
        mockMvc.perform(put("/api/prescription/" + prescriptionId + "/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 4. 验证处方状态已更新
        mockMvc.perform(get("/api/prescription/" + prescriptionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(1)); // 1表示已发药
    }
}
