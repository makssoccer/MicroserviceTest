package ru.sharanov.teacherservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;
import ru.sharanov.teacherservice.dto.StudentMessageKafka;
import ru.sharanov.teacherservice.dto.TeacherResponse;
import ru.sharanov.teacherservice.dto.TeachersResponse;
import ru.sharanov.teacherservice.model.Teacher;
import ru.sharanov.teacherservice.services.TeacherService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping("/teachers")
@Tag(name = "Teacher controller", description = "the controller giving information about all teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final ObjectMapper objectMapper;

    @GetMapping("/{id}")
    @Operation(
            summary = "ont teacher method",
            description = "method that returns information about one teacher"
    )
    public ResponseEntity<?> fetchTeacherById(@PathVariable @Parameter(description = "id teacher") Integer id) {
        Optional<TeacherResponse> teacherResponse = teacherService.fetchTeacherById(id);
        if (teacherResponse.isPresent()) {
            return new ResponseEntity<>(teacherResponse.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Teacher not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping
    @Operation(
            summary = "all teacher method",
            description = "method that returns information about all teachers"
    )
    public ResponseEntity<?> fetchTeacher() {
        Optional<TeachersResponse> teacherResponse = teacherService.fetchTeacher();
        if (teacherResponse.isPresent()) {
            return new ResponseEntity<>(teacherResponse.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Teachers not found", HttpStatus.NOT_FOUND);
    }

    @PostMapping
    @Operation(
            summary = "create teacher method",
            description = "method that create new teacher"
    )

    public ResponseEntity<?> createTeacher(
            @RequestBody @Parameter(description = " info about new teacher") Teacher teacher) {
        Optional<TeacherResponse> teacherResponse = teacherService.createTeacher(teacher);
        if (teacherResponse.isPresent()) {
            return new ResponseEntity<>(teacherResponse.get(), HttpStatus.CREATED);
        }
        log.info("something wrong when create teacher {} ", teacher.getName());
        return new ResponseEntity<>("Teacher don't create", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/students/{studentId}")
    public List<Teacher> getTeachersByStudentId(@PathVariable Long studentId) {
        return teacherService.getTeachersByStudentId(studentId);
    }

    @KafkaListener(topics = "${kafka.topic1}", groupId = "my-group")
    public void receiveStudentMessage(String jsonMessage) throws IOException {
        StudentMessageKafka studentMessage = objectMapper.readValue(jsonMessage, StudentMessageKafka.class);
        teacherService.createStudentAndAssignTeachers(studentMessage.getStudentId(), studentMessage.getTeacherIds());
        log.info("get message: " + studentMessage + " from kafka");
    }
}