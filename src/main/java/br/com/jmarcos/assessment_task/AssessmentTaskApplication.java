package br.com.jmarcos.assessment_task;

import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.com.jmarcos.assessment_task.model.User;
import br.com.jmarcos.assessment_task.model.enums.UserTypeEnum;
import br.com.jmarcos.assessment_task.repository.UserRepository;;

@SpringBootApplication
public class AssessmentTaskApplication implements CommandLineRunner {

	private final UserRepository userRepository;

	public AssessmentTaskApplication(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(AssessmentTaskApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<User> users = this.userRepository.findAll();

		if (users.isEmpty()) {
			User user = new User();
			user.setLogin("000.000.000-00");
			user.setPassword(new BCryptPasswordEncoder().encode("secretary1234"));
			user.setUserType(Set.of(UserTypeEnum.ROLE_SECRETARY));

			this.userRepository.save(user);

		}
	}

}
