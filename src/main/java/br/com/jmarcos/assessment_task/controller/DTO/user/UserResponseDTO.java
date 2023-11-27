package br.com.jmarcos.assessment_task.controller.DTO.user;

import java.util.Set;

import br.com.jmarcos.assessment_task.model.User;
import br.com.jmarcos.assessment_task.model.enums.UserTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;

    private String login;

    private Set<UserTypeEnum> userType;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.userType = user.getUserType();
    }

}
