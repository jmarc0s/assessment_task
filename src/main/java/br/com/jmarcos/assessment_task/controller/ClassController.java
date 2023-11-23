package br.com.jmarcos.assessment_task.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.jmarcos.assessment_task.controller.DTO.classes.ClassRequestDTO;
import br.com.jmarcos.assessment_task.controller.DTO.classes.ClassResponseDTO;
import br.com.jmarcos.assessment_task.service.ClassService;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.jmarcos.assessment_task.model.Class;

@RestController
@RequestMapping("/classes")
public class ClassController {

    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }


    
    @PostMapping
    public ResponseEntity<ClassResponseDTO> save(@RequestBody @Valid ClassRequestDTO classRequest, UriComponentsBuilder uriBuilder) {                          

        Class savedClass = this.classService.save(classRequest);

        URI uri = uriBuilder.path("/classes/{id}").buildAndExpand(savedClass.getId()).toUri();

        return ResponseEntity.created(uri).body(new ClassResponseDTO(savedClass));
    }
    


    
}
