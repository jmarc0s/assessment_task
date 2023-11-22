package br.com.jmarcos.assessment_task.service;

import org.springframework.stereotype.Service;

import br.com.jmarcos.assessment_task.repository.ClassRepository;

@Service
public class ClassService {
    private final ClassRepository classRepository;

    public ClassService(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }
    
}
