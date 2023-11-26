package br.com.jmarcos.assessment_task.controller.DTO.student;

import java.time.LocalDate;
import java.util.Set;

import org.hibernate.validator.constraints.br.CPF;

import br.com.jmarcos.assessment_task.controller.DTO.student.address.AddressRequestDTO;
import br.com.jmarcos.assessment_task.controller.DTO.student.responsible.ResponsibleRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Valid
    private Set<ResponsibleRequestDTO> responsibles;

    @NotNull
    @Valid
    private AddressRequestDTO address;

    @NotNull
    @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$")
    private String dateOfBirth;

    private Long classId;
}
