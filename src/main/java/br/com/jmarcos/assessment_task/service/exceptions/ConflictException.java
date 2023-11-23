package br.com.jmarcos.assessment_task.service.exceptions;

public class ConflictException extends RuntimeException{
    public ConflictException(String detail) {
        super(detail);
    }
}
