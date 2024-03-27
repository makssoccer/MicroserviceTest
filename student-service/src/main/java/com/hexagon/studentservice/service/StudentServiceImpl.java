package com.hexagon.studentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexagon.studentservice.dto.StudentMessageKafka;
import com.hexagon.studentservice.dto.StudentResponse;
import com.hexagon.studentservice.entity.School;
import com.hexagon.studentservice.entity.Student;
import com.hexagon.studentservice.entity.Teacher;
import com.hexagon.studentservice.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl {
    private final KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${kafka.topic1}")
    private String topic;
    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<?> create(Student student)  {
        {
            Student savedStudent = studentRepository.save(student);
            if (student.getTeacherId() != null) {
                StudentMessageKafka studentMessage = new StudentMessageKafka(savedStudent.getId(), student.getTeacherId());
                sendMessage(studentMessage);
            }
            return new ResponseEntity<>(savedStudent, HttpStatus.OK);
        }

    }
    private void sendMessage(StudentMessageKafka studentMessage) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(studentMessage);
            kafkaTemplate.send(new ProducerRecord<>(topic, jsonMessage));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public ResponseEntity<?> getById(Long id) {
        Optional<Student> student = studentRepository.findById(id);
        if (student.isPresent()) {
            School school = restTemplate.getForObject("http://localhost:8080/school/" + student.get().getSchoolId(), School.class);

            ParameterizedTypeReference<List<Teacher>> responseType = new ParameterizedTypeReference<List<Teacher>>() {};
            ResponseEntity<List<Teacher>> responseEntity = restTemplate.exchange("http://localhost:8085/teachers/students/1", HttpMethod.GET, null, responseType);
            List<Teacher> teachers = responseEntity.getBody();
            StudentResponse studentResponse = new StudentResponse(
                    student.get().getId(),
                    student.get().getName(),
                    student.get().getAge(),
                    student.get().getGender(),
                    school,
                    teachers
            );
            return new ResponseEntity<>(studentResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No Student Found", HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> getAll() {
        List<Student> students = studentRepository.findAll();
        if (students.size() > 0) {
            return new ResponseEntity<List<Student>>(students, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No Students", HttpStatus.NOT_FOUND);
        }
    }
    @Transactional
    public ResponseEntity<?> update(Long id, Student student) {

        Optional<Student> oldStudent = studentRepository.findById(id);
        if (oldStudent.isPresent()) {
            studentRepository.updateStudent( student.getName(), student.getAge(), student.getGender(),
                    student.getSchoolId(), id);

            student.setId(id);
            return new ResponseEntity<>(student, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No Student Found", HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> delete(Long id) {
        Optional<Student> student = studentRepository.findById(id);
        if (student.isPresent()) {
            studentRepository.delete(student.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No Student Found", HttpStatus.NOT_FOUND);
        }
    }


}