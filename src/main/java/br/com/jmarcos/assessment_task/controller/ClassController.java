package br.com.jmarcos.assessment_task.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.jmarcos.assessment_task.controller.DTO.classes.ClassRequestDTO;
import br.com.jmarcos.assessment_task.controller.DTO.classes.ClassResponseDTO;
import br.com.jmarcos.assessment_task.service.ClassService;
import jakarta.validation.Valid;

import java.net.URI;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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


    @GetMapping
    public Page<ClassResponseDTO> search(Pageable pageable){
        return this.classService
                .search(pageable)
                .map(ClassResponseDTO::new);
    }
    
    @PostMapping
    public ResponseEntity<ClassResponseDTO> save(@RequestBody @Valid ClassRequestDTO classRequest, UriComponentsBuilder uriBuilder) {                          

        Class savedClass = this.classService.save(classRequest);

        URI uri = uriBuilder.path("/classes/{id}").buildAndExpand(savedClass.getId()).toUri();

        return ResponseEntity.created(uri).body(new ClassResponseDTO(savedClass));
    }
    


    
}
