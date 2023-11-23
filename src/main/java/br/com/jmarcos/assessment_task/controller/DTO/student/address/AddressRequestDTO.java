package br.com.jmarcos.assessment_task.controller.DTO.student.address;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressRequestDTO {


    @NotNull
    @NotBlank
    private String street;

    @NotNull
    @Positive
    private int number;

    @NotNull
    @NotBlank
    private String neighborhood;

    @NotNull
    @NotBlank
    private String complement;
}
