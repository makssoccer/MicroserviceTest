package com.hexagon.studentservice.repository;


import com.hexagon.studentservice.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Modifying
    @Query("update Student s set  s.name = :name, s.age = :age, s.gender = :gender, " +
            "s.schoolId = :schoolId where s.id = :id")
    void updateStudent(@Param("name")String name,
                       @Param("age")Integer age,
                       @Param("gender")String gender,
                       @Param("schoolId")Integer schoolId,
                       @Param("id") Long id);
}