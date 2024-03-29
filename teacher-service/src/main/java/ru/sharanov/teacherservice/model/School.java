package ru.sharanov.teacherservice.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class School {
    private Long id;
    private String schoolName;
    private String location;
    private String principalName;
}
