package com.hexagon.studentservice.unit.service;

import com.hexagon.studentservice.dto.StudentResponse;
import com.hexagon.studentservice.entity.School;
import com.hexagon.studentservice.entity.Student;
import com.hexagon.studentservice.repository.StudentRepository;
import com.hexagon.studentservice.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private StudentService studentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreate() {
        Student student = new Student();
        student.setName("John Doe");
        student.setAge(25);
        student.setGender("Male");
        student.setSchoolId(1);

        when(studentRepository.save(student)).thenReturn(student);

        ResponseEntity<?> response = studentService.create(student);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(student, response.getBody());
    }

    @Test
    public void testGetById() {
        Long id = 1L;
        Student student = new Student();
        student.setId(id);
        student.setName("John Doe");
        student.setAge(25);
        student.setGender("Male");
        student.setSchoolId(1);

        School school = new School();
        school.setId(1L);
        school.setSchoolName("Test School");

        when(studentRepository.findById(id)).thenReturn(Optional.of(student));
        when(restTemplate.getForObject("http://localhost:8080/school/" + student.getSchoolId(), School.class)).thenReturn(school);

        ResponseEntity<?> response = studentService.getById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(student.getId(), ((StudentResponse) response.getBody()).getId());
        assertEquals(student.getName(), ((StudentResponse) response.getBody()).getName());
        assertEquals(student.getAge(), ((StudentResponse) response.getBody()).getAge());
        assertEquals(student.getGender(), ((StudentResponse) response.getBody()).getGender());
        assertEquals(school, ((StudentResponse) response.getBody()).getSchool());
    }

    @Test
    public void testGetAll() {
        Student student1 = new Student();
        student1.setId(1L);
        student1.setName("John Doe");
        student1.setAge(25);
        student1.setGender("Male");
        student1.setSchoolId(1);

        Student student2 = new Student();
        student2.setId(2L);
        student2.setName("Jane Doe");
        student2.setAge(30);
        student2.setGender("Female");
        student2.setSchoolId(2);

        List<Student> students = List.of(student1, student2);

        when(studentRepository.findAll()).thenReturn(students);

        ResponseEntity<?> response = studentService.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(students, response.getBody());
    }

    @Test
    public void testUpdate() {
        Long id = 1L;
        Student student = new Student();
        student.setId(id);
        student.setName("John Doe");
        student.setAge(25);
        student.setGender("Male");
        student.setSchoolId(1);

        Student oldStudent = new Student();
        oldStudent.setId(id);
        oldStudent.setName("Old John Doe");
        oldStudent.setAge(20);
        oldStudent.setGender("Male");
        oldStudent.setSchoolId(1);

        when(studentRepository.findById(id)).thenReturn(Optional.of(oldStudent));

        ResponseEntity<?> response = studentService.update(id, student);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(student, response.getBody());
    }

    @Test
    public void testDelete() {
        Long id = 1L;
        Student student = new Student();
        student.setId(id);

        when(studentRepository.findById(id)).thenReturn(Optional.of(student));

        ResponseEntity<?> response = studentService.delete(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}