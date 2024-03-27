package com.hexagon.studentservice.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StudentMessageKafka {
    private Long studentId;
    private List<Integer> teacherIds;
}
