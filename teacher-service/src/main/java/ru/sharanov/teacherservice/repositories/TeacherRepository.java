package ru.sharanov.teacherservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sharanov.teacherservice.model.Teacher;

import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    @Query("SELECT t FROM Teacher t JOIN t.student s WHERE s.id = :studentId")
    List<Teacher> findByStudentsId(Long studentId);
}