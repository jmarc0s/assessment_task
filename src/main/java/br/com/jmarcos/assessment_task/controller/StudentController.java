package br.com.jmarcos.assessment_task.controller;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.jmarcos.assessment_task.controller.DTO.classes.ClassResponseDTO;
import br.com.jmarcos.assessment_task.controller.DTO.student.StudentRequestDTO;
import br.com.jmarcos.assessment_task.controller.DTO.student.StudentResponseDTO;
import br.com.jmarcos.assessment_task.model.Student;
import br.com.jmarcos.assessment_task.model.User;
import br.com.jmarcos.assessment_task.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/students")
public class StudentController {
        private final StudentService studentService;

        public StudentController(StudentService studentService) {
                this.studentService = studentService;
        }

        @Operation(summary = "Returns a list of students", description = "Returns a list of all students in database.", responses = {
                        @ApiResponse(responseCode = "200", description = "list returned successfully"),
                        @ApiResponse(responseCode = "500", description = "something went wrong"),
                        @ApiResponse(responseCode = "403", description = "access denied")
        })

        @GetMapping
        public List<StudentResponseDTO> search(@RequestParam(required = false) Pageable pageable) {
                return this.studentService
                                .search(pageable)
                                .map(StudentResponseDTO::new)
                                .toList();
        }

        @Operation(summary = "return a student by id", description = "return student by the specified id", responses = {
                        @ApiResponse(responseCode = "200", description = "student returned successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StudentResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "the submitted id is not a number"),
                        @ApiResponse(responseCode = "403", description = "access denied"),
                        @ApiResponse(responseCode = "404", description = "student not found with the specified id")
        })
        @GetMapping("/{id}")
        public ResponseEntity<StudentResponseDTO> searchById(@PathVariable Long id) {

                return ResponseEntity.ok(new StudentResponseDTO(this.studentService.findById(id)));
        }

        @Operation(summary = "record a new student", description = "save a new student in database", responses = {
                        @ApiResponse(responseCode = "201", description = "student recorded successfully"),
                        @ApiResponse(responseCode = "400", description = "You probably filled out a field incorrectly or You probably submitted an invalid date of birth"),
                        @ApiResponse(responseCode = "403", description = "Access denied"),
                        @ApiResponse(responseCode = "409", description = "CPF is already in use by someone else")
        })

        @PostMapping
        public ResponseEntity<StudentResponseDTO> save(@RequestBody @Valid StudentRequestDTO studentRequest,
                        UriComponentsBuilder uriBuilder) {

                Student savedStudent = this.studentService.save(studentRequest);

                URI uri = uriBuilder.path("/students/{id}").buildAndExpand(savedStudent.getId()).toUri();

                return ResponseEntity.created(uri).body(new StudentResponseDTO(savedStudent));
        }

        @Operation(summary = "delete a student by id", description = "delete a student by the specified id", responses = {
                        @ApiResponse(responseCode = "204", description = "student deleted"),
                        @ApiResponse(responseCode = "400", description = "The submitted id is not a number"),
                        @ApiResponse(responseCode = "403", description = "access denied"),
                        @ApiResponse(responseCode = "404", description = "student not found with the specified id")

        })

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(@PathVariable Long id) {
                this.studentService.delete(id);

                return ResponseEntity.noContent().build();

        }

        @Operation(summary = "update student", description = "update all data student", responses = {
                        @ApiResponse(responseCode = "200", description = "student was updated"),
                        @ApiResponse(responseCode = "400", description = "You probably filled out a field incorrectly"),
                        @ApiResponse(responseCode = "403", description = "Access denied"),
                        @ApiResponse(responseCode = "409", description = "This cpf is already in use by someone else")

        })

        @PutMapping("/{id}")
        public ResponseEntity<StudentResponseDTO> update(@RequestBody @Valid StudentRequestDTO studentRequestDTO,
                        @PathVariable Long id) {

                Student updatedStudent = this.studentService
                                .update(studentRequestDTO, id);

                return ResponseEntity.ok(new StudentResponseDTO(updatedStudent));

        }

        @Operation(summary = "returns to the student class", description = "returns to the student class", responses = {
                        @ApiResponse(responseCode = "200", description = "class returned successfully"),
                        @ApiResponse(responseCode = "403", description = "access denied"),
        })

        @GetMapping("/class")
        public ResponseEntity<ClassResponseDTO> getClass(@AuthenticationPrincipal User user) {

                return ResponseEntity.ok(new ClassResponseDTO(this.studentService.findMyClass(user)));
        }
}
