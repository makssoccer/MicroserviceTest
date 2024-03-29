package com.hexagon.studentservice.unit.controller;


import static org.mockito.Mockito.doReturn;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexagon.studentservice.controller.StudentController;
import com.hexagon.studentservice.dto.StudentResponse;
import com.hexagon.studentservice.entity.Student;
import com.hexagon.studentservice.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;


import java.util.Collections;
import java.util.List;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Test
    public void testFetchStudentById() throws Exception {
        StudentResponse studentResponse = new StudentResponse();
        studentResponse.setId(1L);
        studentResponse.setName("John");
        // Создание ResponseEntity с явным указанием типа подстановки
        ResponseEntity<StudentResponse> responseEntity = new ResponseEntity<>(studentResponse, HttpStatus.OK);

        // Мокирование сервиса для успешного выполнения
        doReturn(responseEntity).when(studentService).getById(1L);

        // Проверка успешного выполнения запроса
        mockMvc.perform(MockMvcRequestBuilders.get("/student/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("John"));

        // Подготовка данных для случая, когда студент не найден
        doReturn(new ResponseEntity<>("No Student Found", HttpStatus.NOT_FOUND)).when(studentService).getById(2L);

        // Проверка случая, когда студент не найден
        mockMvc.perform(MockMvcRequestBuilders.get("/student/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testFetchStudents() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("John");
        ResponseEntity<List<Student>> responseEntity = new ResponseEntity<>(Collections.singletonList(student), HttpStatus.OK);

        // Мокирование сервиса для успешного выполнения
        doReturn(responseEntity).when(studentService).getAll();


        // Проверка успешного выполнения запроса
        mockMvc.perform(MockMvcRequestBuilders.get("/student")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("John"));
    }

    @Test
    public void testCreateStudent() throws Exception {
        // Подготовка данных для успешного выполнения
        Student student = new Student();
        student.setId(1L);
        student.setName("John");
        ResponseEntity<?> responseEntity = ResponseEntity.ok(student);

        // Мокирование сервиса для успешного выполнения
        doReturn(responseEntity).when(studentService).create(student);

        // Проверка успешного выполнения запроса
        mockMvc.perform(MockMvcRequestBuilders.post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(student)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testEditStudent() throws Exception {
        // Подготовка данных для успешного выполнения
        Long id = 1L;
        Student student = new Student();
        student.setId(id);
        student.setName("John");
        ResponseEntity<?> responseEntity = ResponseEntity.ok(student);

        // Мокирование сервиса для успешного выполнения
        doReturn(responseEntity).when(studentService).update(id, student);

        // Проверка успешного выполнения запроса
        mockMvc.perform(MockMvcRequestBuilders.put("/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(student)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDeleteStudent() throws Exception {
        // Подготовка данных для успешного выполнения
        Long id = 1L;
        ResponseEntity<?> responseEntity = ResponseEntity.ok().build();

        // Мокирование сервиса для успешного выполнения
        doReturn(responseEntity).when(studentService).delete(id);

        // Проверка успешного выполнения запроса
        mockMvc.perform(MockMvcRequestBuilders.delete("/student/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    // Метод для преобразования объекта в JSON
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
