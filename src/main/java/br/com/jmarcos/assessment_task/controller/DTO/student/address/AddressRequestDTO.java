package br.com.jmarcos.assessment_task.controller.DTO.student.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
