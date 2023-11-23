package br.com.jmarcos.assessment_task.controller.DTO.student.address;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressResponseDTO {
    
    private String street;

    private int number;

    private String neighborhood;

    private String complement;
}
