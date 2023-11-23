package br.com.jmarcos.assessment_task.controller.DTO.student;

import java.time.LocalDate;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import org.hibernate.validator.constraints.br.CPF;

import br.com.jmarcos.assessment_task.controller.DTO.student.address.AddressRequestDTO;
import br.com.jmarcos.assessment_task.controller.DTO.student.responsible.ResposibleRequestDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentRequestDTO {

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    @CPF
    private String cpf;

    @NotNull
    @NotEmpty
    private Set<ResposibleRequestDTO> responsibles;

    @NotNull
    @Valid
    private AddressRequestDTO address;

    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    @PositiveOrZero
    private Long classId;
}
