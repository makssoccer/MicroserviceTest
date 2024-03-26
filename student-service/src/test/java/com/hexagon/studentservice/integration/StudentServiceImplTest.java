package com.hexagon.studentservice.integration;

import com.hexagon.studentservice.dto.StudentResponse;
import com.hexagon.studentservice.entity.School;
import com.hexagon.studentservice.entity.Student;
import com.hexagon.studentservice.repository.StudentRepository;
import com.hexagon.studentservice.service.StudentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

    @Mock
    private RestTemplate restTemplate;

    @Test
    public void testCreateStudent() {
        Student studentToSave = new Student();
        studentToSave.setName("Test Name");
        studentToSave.setAge(20);
        studentToSave.setGender("Male");
        studentToSave.setSchoolId(1);

        Student savedStudent = new Student();
        savedStudent.setId(1L); // Предполагается, что ID генерируется автоматически
        savedStudent.setName(studentToSave.getName());
        savedStudent.setAge(studentToSave.getAge());
        savedStudent.setGender(studentToSave.getGender());
        savedStudent.setSchoolId(studentToSave.getSchoolId());

        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

        ResponseEntity<?> response = studentService.create(studentToSave);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be OK"),
                () -> assertNotNull(response.getBody(), "Response body should not be null"),
                () -> assertEquals(savedStudent, response.getBody(), "Body should be the saved student"),
                () -> assertNotNull(((Student) response.getBody()).getId(), "Saved student should have non-null ID")
        );
    }

    @Test
    public void testGetStudentById() {
        Student student = new Student();
        student.setId(1L);
        student.setSchoolId(1);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        School school = new School();
        school.setId(1L);
        when(restTemplate.getForObject(anyString(), eq(School.class))).thenReturn(school);

        ResponseEntity<?> response = studentService.getById(1L);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be OK"),
                () -> assertNotNull(response.getBody(), "Response body should not be null"),
                () -> assertTrue(response.getBody() instanceof StudentResponse, "Body should be an instance of StudentResponse")
        );
    }

    @Test
    public void testGetAllStudents() {
        List<Student> students = new ArrayList<>();
        students.add(new Student());
        when(studentRepository.findAll()).thenReturn(students);

        ResponseEntity<?> response = studentService.getAll();

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be OK"),
                () -> assertNotNull(response.getBody(), "Body should not be null"),
                () -> assertTrue(response.getBody() instanceof List, "Body should be an instance of List"),
                () -> assertFalse(((List<?>) response.getBody()).isEmpty(), "List should not be empty")
        );
    }

    @Test
    public void testUpdateStudent() {
        Student student = new Student();
        student.setId(1L);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        ResponseEntity<?> response = studentService.update(1L, student);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be OK"),
                () -> assertEquals(student, response.getBody(), "Body should be the updated student")
        );
    }

    @Test
    public void testDeleteStudent() {
        Student student = new Student();
        student.setId(1L);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        ResponseEntity<?> response = studentService.delete(1L);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be OK"),
                () -> verify(studentRepository, times(1)).delete(student)
        );
    }
}