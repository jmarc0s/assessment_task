package br.com.jmarcos.assessment_task.controller.DTO.user;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {
    @NotBlank
    @NotNull
    private String login;

    @NotBlank
    @NotNull
    private String password;

    public UsernamePasswordAuthenticationToken convert() {

        return new UsernamePasswordAuthenticationToken(login, password);
    }
}
