package br.com.jmarcos.assessment_task.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.jmarcos.assessment_task.model.User;
import br.com.jmarcos.assessment_task.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticateTokenFilter extends OncePerRequestFilter {
    private TokenService tokenService;
    private UserRepository userRepository;

    public AuthenticateTokenFilter(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = this.getToken(request);
        if (this.tokenService.isAValidToken(token)) {
            this.authenticateUser(token);
        }

        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
            return null;
        }
        return token.substring(7, token.length());
    }

    private void authenticateUser(String token) {
        Long userId = this.tokenService.getUserId(token);
        Optional<User> user = userRepository.findById(userId);
        UsernamePasswordAuthenticationToken authUser = new UsernamePasswordAuthenticationToken(user.get(), null,
                user.get().getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authUser);
    }
}
