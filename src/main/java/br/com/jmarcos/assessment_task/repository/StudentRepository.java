package br.com.jmarcos.assessment_task.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.jmarcos.assessment_task.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByCpf(String cpf);

    Optional<Student> findByCpf(String cpf);

}
