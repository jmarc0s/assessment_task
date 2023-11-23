package br.com.jmarcos.assessment_task.configurations;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionDetails {
    private String title;

    private String details;

    private int status;

    private List<ArgumentNotValidDetails> fields;

    public ExceptionDetails(String title, String details, int status){
        this.title = title;
        this.details = details;
        this.status = status;
    }
}
