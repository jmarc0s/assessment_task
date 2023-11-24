package br.com.jmarcos.assessment_task.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.jmarcos.assessment_task.controller.DTO.classes.ClassRequestDTO;
import br.com.jmarcos.assessment_task.controller.DTO.classes.ClassResponseDTO;
import br.com.jmarcos.assessment_task.service.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.jmarcos.assessment_task.model.Class;

@RestController
@RequestMapping("/classes")
public class ClassController {

    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @Operation(summary = "Returns a list of classes", description = "Returns a list of all classes in database.", responses = {
            @ApiResponse(responseCode = "200", description = "list returned successfully"),
            @ApiResponse(responseCode = "403", description = "access denied")
    })

    @GetMapping
    public Page<ClassResponseDTO> search(Pageable pageable) {
        return this.classService
                .search(pageable)
                .map(ClassResponseDTO::new);
    }

    @Operation(summary = "returns a class by id", description = "returns class by the specified id", responses = {
            @ApiResponse(responseCode = "200", description = "class returned successfully"),
            @ApiResponse(responseCode = "400", description = "the submitted id is not a number"),
            @ApiResponse(responseCode = "403", description = "access denied"),
            @ApiResponse(responseCode = "404", description = "class not found with the specified id")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClassResponseDTO> searchById(@PathVariable Long id) {
        Class returnedClass = this.classService.findById(id);

        return ResponseEntity.ok(new ClassResponseDTO(returnedClass));
    }

    @Operation(summary = "record a new class", description = "save a new class in database", responses = {
            @ApiResponse(responseCode = "201", description = "Class created successfully"),
            @ApiResponse(responseCode = "400", description = "You probably filled out a field incorrectly or you're trying set class status as active but it don't have a teacher"),
            @ApiResponse(responseCode = "403", description = "Acess denied"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "409", description = "This student is already allocated to another class or this teacher cannot be assigned to this class this shift")
    })

    @PostMapping
    public ResponseEntity<ClassResponseDTO> save(@RequestBody @Valid ClassRequestDTO classRequest,
            UriComponentsBuilder uriBuilder) {

        Class savedClass = this.classService.save(classRequest);

        URI uri = uriBuilder.path("/classes/{id}").buildAndExpand(savedClass.getId()).toUri();

        return ResponseEntity.created(uri).body(new ClassResponseDTO(savedClass));
    }

    @Operation(summary = "delete a publishing Company by id", description = "delete a publishing company by the specified id from database", responses = {
            @ApiResponse(responseCode = "204", description = "Class deleted"),
            @ApiResponse(responseCode = "400", description = "The submitted id is not a number"),
            @ApiResponse(responseCode = "403", description = "access denied"),
            @ApiResponse(responseCode = "404", description = "class not found with the specified id")

    })

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        this.classService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Class was successfully deleted");

    }

    @Operation(summary = "updates a publishing", description = "update data like,name,url,address etc", responses = {
            @ApiResponse(responseCode = "200", ref = "class was updated"),
            @ApiResponse(responseCode = "400", ref = "You probably filled out a field incorrectly or you're trying set class status as active but it don't have a teacher"),
            @ApiResponse(responseCode = "403", ref = "Access denied"),
            @ApiResponse(responseCode = "404", ref = "Student not found"),
            @ApiResponse(responseCode = "409", ref = "This student is already allocated to another class or this teacher cannot be assigned to this class this shift")

    })

    @PutMapping("/{id}")
    public ResponseEntity<ClassResponseDTO> update(@RequestBody @Valid ClassRequestDTO classRequestDTO,
            @PathVariable Long id) {

        Class updattedClass = this.classService
                .update(classRequestDTO, id);

        return ResponseEntity.ok(new ClassResponseDTO(updattedClass));

    }

}
