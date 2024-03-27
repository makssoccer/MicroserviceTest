package com.hexagon.studentservice.dto;

import com.hexagon.studentservice.entity.School;
import com.hexagon.studentservice.entity.Teacher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponse {
    private Long id;
    private String name;
    private Integer age;
    private String gender;
    private School school;
    private List<Teacher> teachers;
}