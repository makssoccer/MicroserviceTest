package ru.sharanov.teacherservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sharanov.teacherservice.model.Teacher;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeachersResponse {
    List<Teacher> teachers = new ArrayList<>();
 }
