package br.com.jmarcos.assessment_task.controller.DTO.student;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.jmarcos.assessment_task.controller.DTO.student.address.AddressResponseDTO;
import br.com.jmarcos.assessment_task.controller.DTO.student.responsible.ResponsibleResponseDTO;
import br.com.jmarcos.assessment_task.model.Student;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentResponseDTO {

    private Long id;

    private String name;

    private String cpf;

    private Set<ResponsibleResponseDTO> responsibles;

    private AddressResponseDTO address;

    private LocalDate dateOfBirth;

    private Long classId;

    public StudentResponseDTO(Student savedStudent) {
        this.id = savedStudent.getId();
        this.name = savedStudent.getName();
        this.cpf = savedStudent.getCpf();
        this.dateOfBirth = savedStudent.getDateOfBirth();
        this.address = new AddressResponseDTO(savedStudent.getAddress());
        // this.classId = savedStudent.getClassId().getId();

        this.responsibles = savedStudent.getResponsibles().stream()
                .map(responsible -> new ResponsibleResponseDTO(responsible)).collect(Collectors.toSet());
    }
}
