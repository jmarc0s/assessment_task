package br.com.jmarcos.assessment_task.controller.DTO.student.address;

import br.com.jmarcos.assessment_task.model.Address;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressResponseDTO {

    private String street;

    private int number;

    private String neighborhood;

    private String complement;

    public AddressResponseDTO(Address address) {
        this.street = address.getStreet();
        this.number = address.getNumber();
        this.neighborhood = address.getNeighborhood();
        this.complement = address.getComplement();
    }
}
