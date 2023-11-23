package br.com.jmarcos.assessment_task.configurations;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.jmarcos.assessment_task.service.exceptions.BadRequestException;
import br.com.jmarcos.assessment_task.service.exceptions.ConflictException;
import br.com.jmarcos.assessment_task.service.exceptions.ResourceNotFoundException;


@RestControllerAdvice
public class HandlerExceptionConfig {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionDetails> handlerBadRequestException(BadRequestException exception) {
        ExceptionDetails details = new ExceptionDetails("Bad Request Exception. Please, Submit a Valid Request",
                exception.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionDetails> handlerResourceNotFoundException(ResourceNotFoundException exception) {
        ExceptionDetails details = new ExceptionDetails("Resource Dot Found in Database", exception.getMessage(),
                HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(details, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ExceptionDetails> handlerConflictException(ConflictException exception) {
        ExceptionDetails details = new ExceptionDetails("Data Conflict", exception.getMessage(),
                HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(details, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDetails> handlerMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        ExceptionDetails details = new ExceptionDetails("Bad request, fields are not filled in correctly",
                "the following fields were filled in incorrectly", HttpStatus.BAD_REQUEST.value());
        List<ArgumentNotValidDetails> argumentNotValidDetails = fieldErrors.stream().map(ArgumentNotValidDetails::new)
                .collect(Collectors.toList());
        details.setFields(argumentNotValidDetails);

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionDetails> handlerHttpMessageNotReadableException(
            HttpMessageNotReadableException exception) {
        ExceptionDetails details = new ExceptionDetails("Bad request. syntax error",
                exception.getMessage(),
                HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }


}
