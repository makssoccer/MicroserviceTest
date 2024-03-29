package ru.sharanov.teacherservice.dto;

import lombok.*;
import ru.sharanov.teacherservice.model.School;
import ru.sharanov.teacherservice.model.Student;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherResponse {
    private Integer id;
    private String name;
    private int age;
    private String direction;
    private int salary;
    private School school;
    private List<Student> students;
}