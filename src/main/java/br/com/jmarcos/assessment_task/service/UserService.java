package br.com.jmarcos.assessment_task.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.jmarcos.assessment_task.controller.DTO.user.UserRequestDTO;
import br.com.jmarcos.assessment_task.model.User;
import br.com.jmarcos.assessment_task.model.enums.UserTypeEnum;
import br.com.jmarcos.assessment_task.repository.UserRepository;
import br.com.jmarcos.assessment_task.service.exceptions.ConflictException;
import br.com.jmarcos.assessment_task.service.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> search() {
        return this.userRepository.findAll();
    }

    public User save(@Valid UserRequestDTO userRequestDTO) {
        Optional<User> existUser = this.userRepository.findByLogin(userRequestDTO.getLogin());

        existUser.ifPresent(user -> {
            throw new ConflictException("this cpf is already in use by someone else");
        });

        User user = this.toUser(userRequestDTO);

        return this.userRepository.save(user);
    }

    public User findById(Long id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with the given id"));
    }

    private User toUser(@Valid UserRequestDTO userRequestDTO) {
        User user = new User();

        user.setLogin(userRequestDTO.getLogin());
        user.setPassword(new BCryptPasswordEncoder().encode(userRequestDTO.getPassword()));
        user.setUserType(Set.of(UserTypeEnum.ROLE_SECRETARY));

        return user;
    }

    public User update(UserRequestDTO userRequestDTO,
            Long id) {
        User oldUser = this.findById(id);

        User updatedClass = fillUpdate(oldUser, userRequestDTO);

        return this.userRepository.save(updatedClass);
    }

    private User fillUpdate(User oldUser, UserRequestDTO userRequestDTO) {

        oldUser.setLogin(this.validateLoginUpdate(oldUser.getId(), userRequestDTO.getLogin()));
        oldUser.setPassword(new BCryptPasswordEncoder().encode(userRequestDTO.getPassword()));

        return oldUser;
    }

    private String validateLoginUpdate(Long id, String login) {
        Optional<User> existUser = this.userRepository.findByLogin(login);

        existUser.ifPresent(user -> {
            if (user.getId() != id) {
                throw new ConflictException("Cpf is already in use by someone else");
            }
        });

        return login;
    }
}
