package br.com.jmarcos.assessment_task.service;

import org.springframework.stereotype.Service;

import br.com.jmarcos.assessment_task.model.Student;
import br.com.jmarcos.assessment_task.repository.StudentRepository;
import br.com.jmarcos.assessment_task.service.exceptions.ResourceNotFoundException;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student findById(Long studentId) {
        return this.studentRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with the given id"));
    }
}
