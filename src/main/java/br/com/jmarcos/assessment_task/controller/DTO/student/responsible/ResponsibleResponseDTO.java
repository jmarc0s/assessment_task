package br.com.jmarcos.assessment_task.controller.DTO.student.responsible;

import br.com.jmarcos.assessment_task.model.Responsible;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponsibleResponseDTO {

    private Long id;

    private String name;

    private String email;

    private String phone;

    public ResponsibleResponseDTO(Responsible responsible) {
        this.id = responsible.getId();
        this.name = responsible.getName();
        this.email = responsible.getEmail();
        this.phone = responsible.getPhone();
    }
}
