package br.com.jmarcos.assessment_task.service;

import org.springframework.stereotype.Service;

import br.com.jmarcos.assessment_task.repository.StudentRepository;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
}
