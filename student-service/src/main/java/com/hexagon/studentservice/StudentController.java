package com.hexagon.studentservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private StudentService studentService;
    @GetMapping("/{id}")
    public ResponseEntity<?> fetchStudentById(@PathVariable String id){
        return studentService.fetchStudentById(id);
    }
    @GetMapping
    public ResponseEntity<?> fetchStudents(){
        return studentService.fetchStudents();
    }
    @PostMapping
    public ResponseEntity<?> createStudent(@RequestBody Student student){
        return studentService.createStudent(student);
    }

}