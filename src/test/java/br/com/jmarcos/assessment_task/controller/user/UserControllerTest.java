package br.com.jmarcos.assessment_task.controller.user;

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.jmarcos.assessment_task.controller.UserController;
import br.com.jmarcos.assessment_task.controller.DTO.user.UserRequestDTO;
import br.com.jmarcos.assessment_task.controller.DTO.user.UserResponseDTO;
import br.com.jmarcos.assessment_task.model.User;
import static br.com.jmarcos.assessment_task.model.enums.UserTypeEnum.ROLE_SECRETARY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.jmarcos.assessment_task.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Test
    void shouldReturnAListOfUsersWhenSuccessful() {
        List<User> userList = List.of(this.createUser());
        when(userService.search()).thenReturn(userList);

        List<UserResponseDTO> returnedUserList = userController.searchUsers();

        Assertions.assertFalse(returnedUserList.isEmpty());
        Assertions.assertEquals(userList.size(), returnedUserList.size());
        Assertions.assertEquals(userList.get(0).getId(), returnedUserList.get(0).getId());
        Assertions.assertEquals(userList.get(0).getLogin(), returnedUserList.get(0).getLogin());
        Assertions.assertNotNull(returnedUserList.get(0).getUserType());
        Assertions.assertTrue(userList.get(0).getUserType().containsAll(returnedUserList.get(0).getUserType()));

        verify(userService, times(1)).search();
    }

    @Test
    void shouldReturnAnEmptyListOfUsersWhenSuccessful() {
        List<User> userList = List.of();
        when(userService.search()).thenReturn(userList);

        List<UserResponseDTO> returnedUserList = userController.searchUsers();

        Assertions.assertTrue(returnedUserList.isEmpty());
        Assertions.assertEquals(userList.size(), returnedUserList.size());

        verify(userService, times(1)).search();
    }

    @Test
    void shouldReturnASavedSecretaryWhenSuccessful() {
        UserRequestDTO userRequestDTO = createUserRequestDTO();
        User newUser = createUser();
        when(userService.save(any(UserRequestDTO.class))).thenReturn(newUser);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        ResponseEntity<UserResponseDTO> responseEntitySavedUser = userController.createASecretary(userRequestDTO,
                uriBuilder);

        Assertions.assertNotNull(responseEntitySavedUser);
        assertEquals(HttpStatus.CREATED, responseEntitySavedUser.getStatusCode());
        UserResponseDTO savedUser = responseEntitySavedUser.getBody();

        URI location = responseEntitySavedUser.getHeaders().getLocation();
        Assertions.assertNotNull(location);
        Assertions.assertEquals("/users/" + savedUser.getId(), location.getPath());

        Assertions.assertNotNull(savedUser.getId());
        Assertions.assertEquals(userRequestDTO.getLogin(), savedUser.getLogin());
        Assertions.assertNotNull(savedUser.getUserType());
        Assertions.assertTrue(savedUser.getUserType().contains(ROLE_SECRETARY));

        verify(userService, times(1)).save(any(UserRequestDTO.class));
    }

    @Test
    void shouldThrowsRuntimeExceptionWhenCpfIsAlreadyInUseBySomeoneElse() {
        UserRequestDTO userRequestDTO = createUserRequestDTO();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");
        when(userService.save(any(UserRequestDTO.class)))
                .thenThrow(new RuntimeException("this cpf is already in use by someone else"));

        Assertions.assertThrows(RuntimeException.class,
                () -> userController.createASecretary(userRequestDTO, uriBuilder));

        verify(userService, times(1)).save(any(UserRequestDTO.class));
    }

    @Test
    void shouldReturnAnUserByIdWhenSuccessful() {
        User expectedUser = createUser();
        when(userService.findById(anyLong())).thenReturn(expectedUser);

        ResponseEntity<UserResponseDTO> responseEntityReturnedUser = userController.searchById(1L);

        Assertions.assertNotNull(responseEntityReturnedUser);
        assertEquals(HttpStatus.OK, responseEntityReturnedUser.getStatusCode());
        UserResponseDTO returnedUser = responseEntityReturnedUser.getBody();

        Assertions.assertEquals(expectedUser.getId(), returnedUser.getId());
        Assertions.assertEquals(expectedUser.getLogin(), returnedUser.getLogin());
        Assertions.assertNotNull(returnedUser.getUserType());
        Assertions.assertTrue(expectedUser.getUserType().containsAll(returnedUser.getUserType()));

        verify(userService, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowsRuntimeExceptionWhenClassNotFound() {
        when(userService.findById(anyLong()))
                .thenThrow(new RuntimeException("User not found with the given id"));

        Assertions.assertThrows(RuntimeException.class,
                () -> userController.searchById(anyLong()));

        verify(userService, times(1)).findById(anyLong());
    }

    @Test
    void shouldReturnAnUpdatedUserWhenSuccessful() {
        UserRequestDTO userUpdateRequest = this.createUserRequestDTO();
        User userToBeUpdated = this.createUser();
        when(userService.update(any(UserRequestDTO.class), anyLong())).thenReturn(userToBeUpdated);

        ResponseEntity<UserResponseDTO> responseEntityUpdatedUser = this.userController
                .updateASecretary(userUpdateRequest, userToBeUpdated);

        Assertions.assertNotNull(responseEntityUpdatedUser);
        assertEquals(HttpStatus.OK, responseEntityUpdatedUser.getStatusCode());
        UserResponseDTO updatedUser = responseEntityUpdatedUser.getBody();

        Assertions.assertEquals(userToBeUpdated.getId(), updatedUser.getId());
        Assertions.assertEquals(userUpdateRequest.getLogin(), updatedUser.getLogin());
        Assertions.assertNotNull(updatedUser.getUserType());
        Assertions.assertTrue(updatedUser.getUserType().contains(ROLE_SECRETARY));

        verify(userService, times(1)).update(any(UserRequestDTO.class), anyLong());
    }

    @Test
    void shouldThrowsRuntimeExceptionWhenCpfIsAlreadyInUseBySomeoneElseOnUpdate() {
        UserRequestDTO userUpdateRequest = this.createUserRequestDTO();
        User userToBeUpdated = this.createUser();
        when(userService.update(any(UserRequestDTO.class), anyLong()))
                .thenThrow(new RuntimeException("this cpf is already in use by someone else"));

        Assertions.assertThrows(RuntimeException.class,
                () -> userController.updateASecretary(userUpdateRequest, userToBeUpdated));

        verify(userService, times(1)).update(any(UserRequestDTO.class), anyLong());
    }

    @Test
    void shouldThrowsRuntimeExceptionWhenUserNotFound() {
        UserRequestDTO userUpdateRequest = this.createUserRequestDTO();
        User userToBeUpdated = this.createUser();
        when(userService.update(any(UserRequestDTO.class), anyLong()))
                .thenThrow(new RuntimeException("User not found with the given id"));

        Assertions.assertThrows(RuntimeException.class,
                () -> userController.updateASecretary(userUpdateRequest, userToBeUpdated));

        verify(userService, times(1)).update(any(UserRequestDTO.class), anyLong());
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
