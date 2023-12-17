package br.com.jmarcos.assessment_task.controller.user;

import java.util.Set;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.com.jmarcos.assessment_task.controller.UserController;
import br.com.jmarcos.assessment_task.controller.DTO.user.UserRequestDTO;
import br.com.jmarcos.assessment_task.model.User;
import static br.com.jmarcos.assessment_task.model.enums.UserTypeEnum.ROLE_SECRETARY;
import br.com.jmarcos.assessment_task.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

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

    User createUserToBeUpdated() {
        User user = new User();

        user.setId(2L);
        user.setLogin("111.111.111-11");
        user.setPassword(new BCryptPasswordEncoder().encode("123456"));
        user.setUserType(Set.of(ROLE_SECRETARY));

        return user;
    }
}
