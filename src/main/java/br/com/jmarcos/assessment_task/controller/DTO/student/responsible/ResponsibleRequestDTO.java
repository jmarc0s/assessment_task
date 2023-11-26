package br.com.jmarcos.assessment_task.controller.DTO.student.responsible;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponsibleRequestDTO {

    @NotBlank
    @NotNull
    private String name;

    @NotBlank
    @NotNull
    @Email
    private String email;

    @NotBlank
    @NotNull
    @Pattern(regexp = "\\+?\\d{2}\\s?\\d{4,5}[-\\s]?\\d{4}")
    private String phone;
}
