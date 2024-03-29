package com.hexagon.studentservice.controller;

import com.hexagon.studentservice.service.StudentService;
import com.hexagon.studentservice.entity.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;
    @GetMapping("/{id}")
    public ResponseEntity<?> fetchStudentById(@PathVariable Long id) {
        return studentService.getById(id);
    }

    @GetMapping
    public ResponseEntity<?> fetchStudents() {
        return studentService.getAll();
    }

    @PostMapping
    public ResponseEntity<?> createStudent(@RequestBody Student student) {
        return studentService.create(student);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editStudent(@PathVariable Long id, @RequestBody Student student) {
        return studentService.update(id, student);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> playerPostDelete(@PathVariable Long id) {
        return studentService.delete(id);
    }
}