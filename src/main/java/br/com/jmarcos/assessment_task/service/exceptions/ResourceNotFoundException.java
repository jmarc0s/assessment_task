package br.com.jmarcos.assessment_task.service.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String detail) {
        super(detail);
    }
}
