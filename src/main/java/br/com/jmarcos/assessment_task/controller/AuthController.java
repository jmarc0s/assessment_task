package br.com.jmarcos.assessment_task.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.jmarcos.assessment_task.controller.DTO.token.TokenDTO;
import br.com.jmarcos.assessment_task.controller.DTO.user.UserLoginDTO;
import br.com.jmarcos.assessment_task.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;

    private TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    @Operation(summary = "Login in the system", description = "login to access features", responses = {
            @ApiResponse(responseCode = "200", description = "Successful Request"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "invalid user")
    })
    @PostMapping
    public ResponseEntity<Object> Login(@RequestBody @Valid UserLoginDTO userDTO) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = userDTO.convert();

        try {
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            String token = tokenService.createToken(authentication);

            return ResponseEntity.ok(new TokenDTO(token, "Bearer"));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid parameters");
        }

    }
}
