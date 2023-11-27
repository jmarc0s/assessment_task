package br.com.jmarcos.assessment_task.controller.DTO.user;

import java.util.Set;

import org.hibernate.validator.constraints.br.CPF;

import br.com.jmarcos.assessment_task.model.enums.UserTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    @NotNull
    @CPF
    private String login;

    @NotNull
    @NotBlank
    private String password;

}
