package ru.sharanov.teacherservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "student")
public class Student {

    public Student(long id) {
        this.id = id;
    }

    @Id
    private Long id;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "teacher_student", joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "teacher_id"))
    private List<Teacher> teacher;
}