package br.com.jmarcos.assessment_task.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.jmarcos.assessment_task.controller.DTO.user.UserRequestDTO;
import br.com.jmarcos.assessment_task.controller.DTO.user.UserResponseDTO;
import br.com.jmarcos.assessment_task.model.User;
import br.com.jmarcos.assessment_task.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
        private final UserService userService;

        public UserController(UserService userService) {
                this.userService = userService;
        }

        @Operation(summary = "Returns a list of users", description = "Returns a list of all users in database.", responses = {
                        @ApiResponse(responseCode = "200", description = "list returned successfully"),
                        @ApiResponse(responseCode = "403", description = "access denied")
        })

        @GetMapping
        public List<UserResponseDTO> searchUsers(Pageable pageable) {
                return this.userService
                                .search()
                                .stream()
                                .map(UserResponseDTO::new)
                                .collect(Collectors.toList());
        }

        @Operation(summary = "returns a user by id", description = "returns user by the specified id", responses = {
                        @ApiResponse(responseCode = "200", description = "user returned successfully"),
                        @ApiResponse(responseCode = "400", description = "the submitted id is not a number"),
                        @ApiResponse(responseCode = "403", description = "access denied"),
                        @ApiResponse(responseCode = "404", description = "user not found with the specified id")
        })
        @GetMapping("/{id}")
        public ResponseEntity<UserResponseDTO> searchById(@PathVariable Long id) {
                User user = this.userService.findById(id);

                return ResponseEntity.ok(new UserResponseDTO(user));
        }

        @Operation(summary = "record a new secretary", description = "save a new secretary in database", responses = {
                        @ApiResponse(responseCode = "201", description = "secretary recorded successfully"),
                        @ApiResponse(responseCode = "400", description = "You probably filled out a field incorrectly"),
                        @ApiResponse(responseCode = "403", description = "Access denied"),
                        @ApiResponse(responseCode = "409", description = "CPF is already in use by someone else")
        })

        @PostMapping
        public ResponseEntity<UserResponseDTO> createASecretary(@RequestBody @Valid UserRequestDTO userRequestDTO,
                        UriComponentsBuilder uriBuilder) {

                User savedUser = this.userService.save(userRequestDTO);

                URI uri = uriBuilder.path("/users/{id}").buildAndExpand(savedUser.getId()).toUri();

                return ResponseEntity.created(uri).body(new UserResponseDTO(savedUser));
        }

        @Operation(summary = "update user", description = "update all data user", responses = {
                        @ApiResponse(responseCode = "200", description = "user was updated"),
                        @ApiResponse(responseCode = "400", description = "You probably filled out a field incorrectly"),
                        @ApiResponse(responseCode = "403", description = "Access denied"),
                        @ApiResponse(responseCode = "409", description = "Cpf is already in use by someone else")

        })

        @PutMapping("/{id}")
        public ResponseEntity<UserResponseDTO> updateASecretary(@RequestBody @Valid UserRequestDTO userRequestDTO,
                        @AuthenticationPrincipal User user) {

                User updatedUser = this.userService
                                .update(userRequestDTO, user.getId());

                return ResponseEntity.ok(new UserResponseDTO(updatedUser));

        }

}
