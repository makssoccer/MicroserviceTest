package com.hexagon.studentservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Teacher {
    private String name;
    private Integer age;
    private String direction;
    private Integer salary;

}
