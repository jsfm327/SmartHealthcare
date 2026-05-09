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

import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 病历完整链路测试
 * 测试流程：挂号 → 创建病历 → 查询病历 → 更新病历
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("病历完整链路测试")
class MedicalRecordFlowTest {

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
    @DisplayName("创建病历流程测试")
    void testCreateMedicalRecordFlow() throws Exception {
        // 1. 创建病历
        MedicalRecordDTO recordDTO = new MedicalRecordDTO();
        recordDTO.setPatientId(patientId);
        recordDTO.setDoctorId(doctorId);
        recordDTO.setRegistrationId(registrationId);
        recordDTO.setChiefComplaint("发热3天，咳嗽");
        recordDTO.setPresentIllness("3天前开始发热，最高38.5℃，伴有咳嗽");
        recordDTO.setPastHistory("无特殊病史");
        recordDTO.setPhysicalExam("T:38.2℃, 咽部充血");
        recordDTO.setDiagnosis("上呼吸道感染");
        recordDTO.setTreatmentPlan("口服药物治疗");
        recordDTO.setDoctorAdvice("多饮水，注意休息");
        recordDTO.setVisitDate(LocalDate.now());

        mockMvc.perform(post("/api/medical-record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recordDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 2. 按患者ID查询病历列表
        mockMvc.perform(get("/api/medical-record/patient/" + patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].diagnosis").value("上呼吸道感染"));

        // 3. 获取病历详情
        MvcResult listResult = mockMvc.perform(get("/api/medical-record/patient/" + patientId))
                .andReturn();

        Long recordId = objectMapper.readTree(listResult.getResponse().getContentAsString())
                .path("data").get(0).path("id").asLong();

        mockMvc.perform(get("/api/medical-record/" + recordId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.chiefComplaint").value("发热3天，咳嗽"))
                .andExpect(jsonPath("$.data.diagnosis").value("上呼吸道感染"));
    }

    @Test
    @Order(2)
    @DisplayName("更新病历流程测试")
    void testUpdateMedicalRecordFlow() throws Exception {
        // 1. 先创建病历
        MedicalRecordDTO recordDTO = new MedicalRecordDTO();
        recordDTO.setPatientId(patientId);
        recordDTO.setDoctorId(doctorId);
        recordDTO.setRegistrationId(registrationId);
        recordDTO.setChiefComplaint("初始主诉");
        recordDTO.setDiagnosis("初始诊断");
        recordDTO.setVisitDate(LocalDate.now());

        mockMvc.perform(post("/api/medical-record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recordDTO)))
                .andExpect(status().isOk());

        // 2. 获取病历ID
        MvcResult listResult = mockMvc.perform(get("/api/medical-record/patient/" + patientId))
                .andReturn();

        Long recordId = objectMapper.readTree(listResult.getResponse().getContentAsString())
                .path("data").get(0).path("id").asLong();

        // 3. 更新病历
        MedicalRecordDTO updateDTO = new MedicalRecordDTO();
        updateDTO.setPatientId(patientId);
        updateDTO.setDoctorId(doctorId);
        updateDTO.setChiefComplaint("更新后的主诉");
        updateDTO.setDiagnosis("更新后的诊断");
        updateDTO.setTreatmentPlan("更新后的治疗方案");
        updateDTO.setVisitDate(LocalDate.now());

        mockMvc.perform(put("/api/medical-record/" + recordId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 4. 验证更新结果
        mockMvc.perform(get("/api/medical-record/" + recordId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.chiefComplaint").value("更新后的主诉"))
                .andExpect(jsonPath("$.data.diagnosis").value("更新后的诊断"));
    }
}
