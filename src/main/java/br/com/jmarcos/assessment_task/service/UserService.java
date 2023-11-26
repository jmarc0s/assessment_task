package br.com.jmarcos.assessment_task.service;

import org.springframework.stereotype.Service;

import br.com.jmarcos.assessment_task.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
