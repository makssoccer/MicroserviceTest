package ru.sharanov.teacherservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sharanov.teacherservice.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
