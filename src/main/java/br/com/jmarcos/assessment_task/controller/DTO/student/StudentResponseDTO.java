package br.com.jmarcos.assessment_task.controller.DTO.student;

import java.time.LocalDate;
import java.util.Set;

import br.com.jmarcos.assessment_task.controller.DTO.student.address.AddressResponseDTO;
import br.com.jmarcos.assessment_task.model.Resposible;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class StudentResponseDTO {

    private Long id;

    private String name;

    private String cpf;

    private Set<Resposible> responsibles;

    private AddressResponseDTO address;

    private LocalDate dateOfBirth;

    private Long classId;
}
