package br.com.jmarcos.assessment_task.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.jmarcos.assessment_task.model.User;
import br.com.jmarcos.assessment_task.repository.UserRepository;

@Service
public class AuthService implements UserDetailsService {

    private UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByLogin(email);
        if (user.isPresent()) {
            return user.get();
        }
        throw new UsernameNotFoundException("invalid data!!");
    }

}
