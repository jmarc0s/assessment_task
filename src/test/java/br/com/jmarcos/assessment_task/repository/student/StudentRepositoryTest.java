package br.com.jmarcos.assessment_task.repository.student;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import br.com.jmarcos.assessment_task.model.Student;
import br.com.jmarcos.assessment_task.repository.StudentRepository;

@DataJpaTest
public class StudentRepositoryTest {

    private StudentRepository studentRepository;

    public StudentRepositoryTest(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Test
    void shouldReturnAStudentByIdWhenSuccessful(){

        List<Student> students = studentRepository.findAll();
        System.out.println(students);
    }
}
