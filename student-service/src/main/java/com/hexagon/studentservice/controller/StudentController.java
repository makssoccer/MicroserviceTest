package com.hexagon.studentservice.controller;

import com.hexagon.studentservice.service.StudentServiceImpl;
import com.hexagon.studentservice.entity.Student;
//import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private StudentServiceImpl studentService;

//    @ApiOperation(value = "Get example data", notes = "This endpoint returns example data")
    @GetMapping("/{id}")
    public ResponseEntity<?> fetchStudentById(@PathVariable Long id){
        return studentService.getById(id);
    }

    @GetMapping
    public ResponseEntity<?> fetchStudents(){
        return studentService.getAll();
    }

    @PostMapping
    public ResponseEntity<?> createStudent(@RequestBody Student student){
        return studentService.create(student);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editStudent(@PathVariable Long id,@RequestBody Student student){
        return studentService.update(id, student);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> playerPostDelete(@PathVariable Long id) {
        return studentService.delete(id);
    }

}