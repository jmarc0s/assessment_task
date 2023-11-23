package br.com.jmarcos.assessment_task.service.exceptions;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String detail) {
        super(detail);
    }
}
