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
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;
import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

    private static final String SCHOOL_SERVICE_URL = "http://localhost:8080/school/";
    private static final String TEACHER_SERVICE_URL = "http://localhost:8085/teachers/students/";
    private static final String NOT_Found = "No Student Found";
    private static final String ERROR = "Error when adding student";
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final StudentRepository studentRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${kafka.topic1}")
    private String topic;
    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<?> create(Student student) {
        log.info("try to add student to BD");
            Student savedStudent = studentRepository.save(student);
            if (student.getTeacherId() != null) {
                StudentMessageKafka studentMessage = new StudentMessageKafka(savedStudent.getId(), student.getTeacherId());
                sendMessage(studentMessage);
            }
        if (student.getId()!=null){
            return new ResponseEntity<>(savedStudent, HttpStatus.OK);}
        else {
            log.warn("couldn't create a student");
            return new ResponseEntity<>(ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private void sendMessage(StudentMessageKafka studentMessage) {
        log.info("I'm trying to convert a message to JSON and send it via Kafka" );
        try {
            String jsonMessage = objectMapper.writeValueAsString(studentMessage);
            kafkaTemplate.send(new ProducerRecord<>(topic, jsonMessage));
        } catch (JsonProcessingException e) {
            log.warn("something went wrong, can't convert the message to JSON", e);
            e.printStackTrace();
        }
    }

    public ResponseEntity<?> getById(Long id) {
        log.info(" try to get the student from the id {}", id );
        Optional<Student> student = studentRepository.findById(id);
        if (student.isPresent()) {
            School school = restTemplate.getForObject(SCHOOL_SERVICE_URL + student.get().getSchoolId(), School.class);

            ParameterizedTypeReference<List<Teacher>> responseType = new ParameterizedTypeReference<List<Teacher>>() {};
            ResponseEntity<List<Teacher>> responseEntity = restTemplate.exchange(TEACHER_SERVICE_URL + id, HttpMethod.GET, null, responseType);
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
            log.warn("user with this id {} not found  ", id);
            return new ResponseEntity<>(NOT_Found, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> getAll() {
        log.info(" try to get All students");
        List<Student> students = studentRepository.findAll();
        if (students.size() > 0) {
            return new ResponseEntity<List<Student>>(students, HttpStatus.OK);
        } else {
            log.warn("users not found ");
            return new ResponseEntity<>(NOT_Found, HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    public ResponseEntity<?> update(Long id, Student student) {
        log.info(" try to update the student from the id {}", id );
        Optional<Student> oldStudent = studentRepository.findById(id);
        if (oldStudent.isPresent()) {
            studentRepository.updateStudent(student.getName(), student.getAge(), student.getGender(),
                    student.getSchoolId(), id);

            student.setId(id);
            return new ResponseEntity<>(student, HttpStatus.OK);
        } else {
            log.warn("user with this id {} not found  ", id);
            return new ResponseEntity<>(NOT_Found, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> delete(Long id) {
        log.info(" try to remove the student from the id {}", id );
        Optional<Student> student = studentRepository.findById(id);
        if (student.isPresent()) {
            studentRepository.delete(student.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            log.warn("user with this id {} not found  ", id);
            return new ResponseEntity<>(NOT_Found, HttpStatus.NOT_FOUND);
        }
    }


}