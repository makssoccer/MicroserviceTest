package ru.sharanov.teacherservice.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.sharanov.teacherservice.controllers.TeacherController;
import ru.sharanov.teacherservice.dto.StudentMessageKafka;
import ru.sharanov.teacherservice.dto.TeacherResponse;
import ru.sharanov.teacherservice.dto.TeachersResponse;
import ru.sharanov.teacherservice.mapper.TeacherMapper;
import ru.sharanov.teacherservice.model.Student;
import ru.sharanov.teacherservice.model.Teacher;
import ru.sharanov.teacherservice.services.TeacherService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TeacherControllerTest {
    @Mock
    private TeacherService teacherService;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TeacherController teacherController;

    private Teacher teacher;
    private TeacherResponse teacherResponse;
    private TeachersResponse teachersResponse;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Student student1 = new Student(1);
        Student student2 = new Student(2);
        List<Student> students = List.of(student1, student2);
        teacher = Teacher.builder()
                .name("Дмитрий Иванов")
                .age(35)
                .salary(45000)
                .direction("Математика")
                .schoolId(1)
                .student(students)
                .build();
        teacherResponse = TeacherMapper.convertTeachertoTeacherResponse(teacher);
        teachersResponse = new TeachersResponse();
        teachersResponse.getTeachers().add(teacher);
    }

    @Test
    public void testFetchTeacherById_Success() {
        int teacherId = 1;
        when(teacherService.fetchTeacherById(teacherId)).thenReturn(Optional.of(teacherResponse));

        ResponseEntity<?> responseEntity = teacherController.fetchTeacherById(teacherId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(teacherResponse, responseEntity.getBody());
        verify(teacherService, times(1)).fetchTeacherById(teacherId);
    }

    @Test
    public void testFetchTeacherById_NotFound() {
        int teacherId = 1;
        when(teacherService.fetchTeacherById(teacherId)).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = teacherController.fetchTeacherById(teacherId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Teacher not found", responseEntity.getBody());
        verify(teacherService, times(1)).fetchTeacherById(teacherId);
    }

    @Test
    public void testFetchTeacher_Success() {
        when(teacherService.fetchTeacher()).thenReturn(Optional.of(teachersResponse));

        ResponseEntity<?> responseEntity = teacherController.fetchTeacher();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(teachersResponse, responseEntity.getBody());
        verify(teacherService, times(1)).fetchTeacher();
    }

    @Test
    public void testCreateTeacher_Success() {
        when(teacherService.createTeacher(teacher)).thenReturn(Optional.of(teacherResponse));

        ResponseEntity<?> responseEntity = teacherController.createTeacher(teacher);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(teacherResponse, responseEntity.getBody());
        verify(teacherService, times(1)).createTeacher(teacher);
    }

    @Test
    public void testCreateTeacher_InternalServerError() {
        when(teacherService.createTeacher(teacher)).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = teacherController.createTeacher(teacher);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Teacher don't create", responseEntity.getBody());
        verify(teacherService, times(1)).createTeacher(teacher);
    }

    @Test
    public void testReceiveStudentMessage_Success() throws IOException {
        String jsonMessage = """
                {"studentId": 1,
                  "teacherIds" : [1, 2, 3]}
                """;
        StudentMessageKafka studentMessageKafka = StudentMessageKafka.builder()
                .studentId(1L)
                .teacherIds(List.of(1, 2, 3))
                .build();

        when(objectMapper.readValue(jsonMessage, StudentMessageKafka.class)).thenReturn(studentMessageKafka);

        teacherController.receiveStudentMessage(jsonMessage);

        verify(teacherService, times(1)).createStudentAndAssignTeachers(
                studentMessageKafka.getStudentId(), studentMessageKafka.getTeacherIds());
    }
}
