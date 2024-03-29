package ru.sharanov.teacherservice.mapper;

import ru.sharanov.teacherservice.dto.TeacherResponse;
import ru.sharanov.teacherservice.model.Teacher;

import java.util.Optional;

public class TeacherMapper {

    public static TeacherResponse convertTeachertoTeacherResponse(Teacher teacher) {
        return TeacherResponse.builder()
                .name(teacher.getName())
                .age(teacher.getAge())
                .direction(teacher.getDirection())
                .salary(teacher.getSalary())
                .id(teacher.getId())
                .build();
    }

    public static TeacherResponse convertTeachertOptionalToTeacherResponse(Optional<Teacher> teacher) {
        return TeacherResponse.builder()
                .name(teacher.get().getName())
                .age(teacher.get().getAge())
                .direction(teacher.get().getDirection())
                .salary(teacher.get().getSalary())
                .id(teacher.get().getId())
                .build();
    }
}
