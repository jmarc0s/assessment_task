package br.com.jmarcos.assessment_task.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.jmarcos.assessment_task.model.Class;
import br.com.jmarcos.assessment_task.model.enums.ClassShiftEnum;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {

    boolean existsByTeacherHolderAndClassShift(String teacherHolder, ClassShiftEnum classShift);

    Optional<Class> findByTeacherHolderAndClassShift(String teacherHolder, ClassShiftEnum classShift);

}
