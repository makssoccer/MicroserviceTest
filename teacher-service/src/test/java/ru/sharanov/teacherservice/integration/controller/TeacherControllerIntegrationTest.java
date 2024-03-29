package ru.sharanov.teacherservice.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.sharanov.teacherservice.dto.TeacherResponse;
import ru.sharanov.teacherservice.dto.TeachersResponse;
import ru.sharanov.teacherservice.mapper.TeacherMapper;
import ru.sharanov.teacherservice.model.Teacher;
import ru.sharanov.teacherservice.repositories.TeacherRepository;
import ru.sharanov.teacherservice.services.TeacherService;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles({"test"})
public class TeacherControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeacherService teacherService;

    @Autowired
    private TeacherRepository teacherRepository;

    private Teacher teacher1;
    private Teacher teacher2;
    private TeacherResponse teacherResponse;
    private TeachersResponse teachersResponse;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        teacher1 = Teacher.builder()
                .name("Дмитрий Иванов")
                .age(35)
                .salary(45000)
                .direction("Математика")
                .schoolId(1)
                .build();
        teacher2 = Teacher.builder()
                .name("Анна Викторовна")
                .age(46)
                .salary(51000)
                .direction("Физика")
                .schoolId(2)
                .build();
        teacherResponse = TeacherMapper.convertTeachertoTeacherResponse(teacher1);
        teachersResponse = new TeachersResponse();
        teachersResponse.getTeachers().add(teacher1);
        teacherRepository.deleteAll();
    }

    @Test
    public void testFetchTeacherById_Success() throws Exception {
        teacher1 = teacherRepository.save(teacher1);
        when(teacherService.fetchTeacherById(teacher1.getId()))
                .thenReturn(Optional.of(teacherResponse));

        mockMvc.perform(get("/teachers/{id}", teacher1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(teacherResponse)));
    }

    @Test
    public void testFetchTeacherById_NotFound() throws Exception {
        int teacherId = 1;
        when(teacherService.fetchTeacherById(teacherId))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/teachers/{id}", teacherId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFetchTeacher_Success() throws Exception {
        teacher1 = teacherRepository.save(teacher1);
        teacher2 = teacherRepository.save(teacher2);
        when(teacherService.fetchTeacher())
                .thenReturn(Optional.of(teachersResponse));

        mockMvc.perform(get("/teachers"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(teachersResponse)));
    }

    @Test
    public void testFetchTeacher_Empty() throws Exception {
        when(teacherService.fetchTeacher())
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/teachers"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateTeacher_Success() throws Exception {
        teacherResponse = TeacherMapper.convertTeachertoTeacherResponse(teacher1);
        when(teacherService.createTeacher(teacher1)).thenReturn(Optional.of(teacherResponse));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(post("/teachers")
                        .headers(headers)
                        .content(objectMapper.writeValueAsBytes(teacher1))
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(teacherResponse)));
    }

    @Test
    public void testCreateTeacher_InternalServerError() throws Exception {
        when(teacherService.createTeacher(teacher1)).thenReturn(Optional.empty());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(post("/teachers")
                        .headers(headers)
                        .content(objectMapper.writeValueAsBytes(teacher1))
                )
                .andExpect(status().isInternalServerError());
    }
}
