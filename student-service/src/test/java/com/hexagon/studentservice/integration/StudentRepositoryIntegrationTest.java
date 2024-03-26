package com.hexagon.studentservice.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import com.hexagon.studentservice.entity.Student;
import com.hexagon.studentservice.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StudentRepositoryIntegrationTest {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    public void testCreateAndGetById() {
        // Создание студента
        Student student = new Student();
        student.setName("John");
        student.setAge(20);
        student.setGender("Male");

        // Сохранение студента
        Student savedStudent = studentRepository.save(student);

        // Получение студента по ID
        Optional<Student> retrievedStudent = studentRepository.findById(savedStudent.getId());

        // Проверка, что студент был успешно сохранен и получен
        assertAll(
                () -> assertNotNull(savedStudent),
                () -> assertEquals("John", retrievedStudent.get().getName())
        );
    }

    @Test
    public void testGetAll() {
        // Получение всех студентов из репозитория
        Iterable<Student> students = studentRepository.findAll();

        // Проверка, что список студентов не пустой
        assertTrue(students.iterator().hasNext());
    }

    @Test
    public void testUpdate() {
        // Создание студента
        Student student = new Student();
        student.setName("John");
        student.setAge(20);
        student.setGender("Male");

        // Сохранение студента
        Student savedStudent = studentRepository.save(student);

        // Изменение имени студента
        savedStudent.setName("Jane");

        // Обновление студента
        studentRepository.save(savedStudent);

        // Получение обновленного студента
        Optional<Student> updatedStudent = studentRepository.findById(savedStudent.getId());

        // Проверка, что студент был успешно обновлен
        assertAll(
                () -> assertEquals("Jane", updatedStudent.get().getName())
        );
    }

    @Test
    public void testDelete() {
        // Создание студента
        Student student = new Student();
        student.setName("John");
        student.setAge(20);
        student.setGender("Male");

        // Сохранение студента
        Student savedStudent = studentRepository.save(student);

        // Удаление студента
        studentRepository.delete(savedStudent);

        // Проверка, что студент был успешно удален
        Optional<Student> deletedStudent = studentRepository.findById(savedStudent.getId());
        assertFalse(deletedStudent.isPresent());
    }
}
