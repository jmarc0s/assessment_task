package br.com.jmarcos.assessment_task.configurations;


import org.springframework.validation.FieldError;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArgumentNotValidDetails {

    private String field;

    private String fieldsMessage;

    public ArgumentNotValidDetails(FieldError fieldError){
        this.field = fieldError.getField();
        this.fieldsMessage = fieldError.getDefaultMessage();
    }

}