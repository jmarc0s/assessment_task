package br.com.jmarcos.assessment_task.service.user;

import static br.com.jmarcos.assessment_task.model.enums.UserTypeEnum.ROLE_SECRETARY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.com.jmarcos.assessment_task.controller.DTO.user.UserRequestDTO;
import br.com.jmarcos.assessment_task.model.User;
import br.com.jmarcos.assessment_task.repository.UserRepository;
import br.com.jmarcos.assessment_task.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void shouldReturnAListOfUsersWhenSuccessful() {
        List<User> userList = List.of(this.createUser());
        when(userRepository.findAll()).thenReturn(userList);

        List<User> returnedUserList = userService.search();

        Assertions.assertFalse(returnedUserList.isEmpty());
        Assertions.assertEquals(userList.get(0).getId(), returnedUserList.get(0).getId());
        Assertions.assertEquals(userList.get(0).getLogin(), returnedUserList.get(0).getLogin());
        Assertions.assertEquals(userList.get(0).getPassword(), returnedUserList.get(0).getPassword());
        Assertions.assertNotNull(returnedUserList.get(0).getUserType());
        Assertions.assertTrue(returnedUserList.get(0).getUserType().containsAll(userList.get(0).getUserType()));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnAnEmptyListOfUsersWhenSuccessful() {
        List<User> userList = List.of();
        when(userRepository.findAll()).thenReturn(userList);

        List<User> returnedUserList = userService.search();

        Assertions.assertTrue(returnedUserList.isEmpty());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnAUserByIdWhenSuccessful() {
        User expectedUser = this.createUser();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(expectedUser));

        User returnedUser = this.userService.findById(1L);

        Assertions.assertEquals(expectedUser.getId(), returnedUser.getId());
        Assertions.assertEquals(expectedUser.getLogin(), returnedUser.getLogin());
        Assertions.assertEquals(expectedUser.getPassword(), returnedUser.getPassword());
        Assertions.assertNotNull(returnedUser.getUserType());
        Assertions.assertTrue(returnedUser.getUserType().containsAll(expectedUser.getUserType()));

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowsRuntimeExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class,
                () -> userService.findById(anyLong()));

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldReturnASavedUserWhenSuccessful() {
        UserRequestDTO userRequest = this.createUserRequestDTO();
        when(userRepository.save(any(User.class))).thenReturn(this.createUser());

        User savedUser = this.userService.save(userRequest);

        Assertions.assertNotNull(savedUser.getId());
        Assertions.assertEquals(userRequest.getLogin(), savedUser.getLogin());
        Assertions.assertNotNull(savedUser.getUserType());
        Assertions
                .assertTrue(new BCryptPasswordEncoder().matches(userRequest.getPassword(), savedUser.getPassword()));
        Assertions.assertTrue(savedUser.getUserType().contains(ROLE_SECRETARY));

        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).findByLogin(anyString());
    }

    @Test
    void shouldThrowsRuntimeExceptionWhenCpfIsAlreadyInUseBySomeoneElse() {
        UserRequestDTO userRequest = this.createUserRequestDTO();
        User user = this.createUser();
        when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(user));

        Assertions.assertThrows(RuntimeException.class,
                () -> userService.save(userRequest));

        verify(userRepository, times(0)).save(any(User.class));
        verify(userRepository, times(1)).findByLogin(anyString());
    }

    @Test
    void shouldReturnAnUpdatedUserWhenSuccessful() {
        UserRequestDTO userRequest = this.createUserRequestDTO();
        User userToBeUpdated = this.createUserToBeUpdated();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userToBeUpdated));
        when(userRepository.save(any(User.class))).thenReturn(userToBeUpdated);

        User updatedUser = this.userService.update(userRequest, 2L);

        Assertions.assertEquals(userToBeUpdated.getId(), updatedUser.getId());
        Assertions.assertEquals(userRequest.getLogin(), updatedUser.getLogin());
        Assertions
                .assertTrue(new BCryptPasswordEncoder().matches(userRequest.getPassword(), updatedUser.getPassword()));
        Assertions.assertNotNull(updatedUser.getUserType());
        Assertions.assertTrue(updatedUser.getUserType().contains(ROLE_SECRETARY));

        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).findByLogin(anyString());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowsRuntimeExceptionWhenCpfIsAlreadyInUseBySomeoneElseOnUpdate() {
        UserRequestDTO userRequest = this.createUserRequestDTO();
        User userToBeUpdated = this.createUserToBeUpdated();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userToBeUpdated));
        when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(this.createUser()));

        Assertions.assertThrows(RuntimeException.class,
                () -> userService.update(userRequest, 2L));

        verify(userRepository, times(0)).save(any(User.class));
        verify(userRepository, times(1)).findByLogin(anyString());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowsRuntimeExceptionWhenUserNotFoundOnUpdate() {
        UserRequestDTO userRequest = this.createUserRequestDTO();
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class,
                () -> userService.update(userRequest, 2L));

        verify(userRepository, times(0)).save(any(User.class));
        verify(userRepository, times(0)).findByLogin(anyString());
        verify(userRepository, times(1)).findById(anyLong());
    }

    private User createUserToBeUpdated() {
        User user = new User();

        user.setId(2L);
        user.setLogin("222.222.222-22");
        user.setPassword(new BCryptPasswordEncoder().encode("654321"));
        user.setUserType(Set.of(ROLE_SECRETARY));

        return user;
    }

    UserRequestDTO createUserRequestDTO() {
        UserRequestDTO user = new UserRequestDTO();

        user.setLogin("111.111.111-11");
        user.setPassword("123456");

        return user;
    }

    User createUser() {
        User user = new User();

        user.setId(1L);
        user.setLogin("111.111.111-11");
        user.setPassword(new BCryptPasswordEncoder().encode("123456"));
        user.setUserType(Set.of(ROLE_SECRETARY));

        return user;
    }

}
