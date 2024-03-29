package ru.sharanov.teacherservice.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentMessageKafka {
    private Long studentId;
    private List<Integer> teacherIds;
}
