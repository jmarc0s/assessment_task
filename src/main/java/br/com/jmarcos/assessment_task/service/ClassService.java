package br.com.jmarcos.assessment_task.service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.jmarcos.assessment_task.controller.DTO.classes.ClassRequestDTO;
import br.com.jmarcos.assessment_task.model.Class;
import br.com.jmarcos.assessment_task.model.Student;
import br.com.jmarcos.assessment_task.model.enums.ClassShiftEnum;
import br.com.jmarcos.assessment_task.model.enums.ClassStatusEnum;
import br.com.jmarcos.assessment_task.repository.ClassRepository;
import br.com.jmarcos.assessment_task.service.exceptions.BadRequestException;
import br.com.jmarcos.assessment_task.service.exceptions.ConflictException;

@Service
public class ClassService {
    private final ClassRepository classRepository;
    private final StudentService studentService;

    public ClassService(ClassRepository classRepository, StudentService studentService) {
        this.classRepository = classRepository;
        this.studentService = studentService;
    }


    public Page<Class> search(Pageable pageable) {
        return this.classRepository.findAll(pageable);
    }


    public Class save(ClassRequestDTO classRequest) {
        Class newClass = this.toClass(classRequest);

        return this.classRepository.save(newClass);
    }


    private Class toClass(ClassRequestDTO classRequest) {
        Class newClass = new Class();

        newClass.setTitle(classRequest.getTitle());
        newClass.setClassStatus(this.validateClassStatus(classRequest.getClassStatus(), classRequest.getTeacherHolder()));
        newClass.setTeacherHolder(this.validateTeacher(classRequest.getTeacherHolder(), classRequest.getClassShift()));
        newClass.setSchoolSegment(classRequest.getSchoolSegment());
        newClass.setMaxStudents(classRequest.getMaxStudents());
        newClass.setClassShift(classRequest.getClassShift());
        newClass.setStudents(this.findStudents(classRequest.getStudentsId()));

        return newClass;
    }
    

    private ClassStatusEnum validateClassStatus(ClassStatusEnum classStatus, String teacherHolder) {
        if(Objects.equals(teacherHolder, null) && Objects.equals(classStatus, ClassStatusEnum.ACTIVE)){
            throw new BadRequestException("The class cannot be active if it does not have a teacher");
        }

        return classStatus;
    }

    private String validateTeacher(String teacherHolder, ClassShiftEnum classShift) {
        if(this.classRepository.existsByTeacherHolderAndClassShift(teacherHolder, classShift)){
            throw new ConflictException("This teacher cannot be assigned to this class this shift");
        }

        return teacherHolder;
    }

    private Set<Student> findStudents(Set<Long> studentsId){

        if(!Objects.equals(studentsId, null)){
            return studentsId.stream()
                    .map(studentId -> this.validateStudent(studentId))
                    .collect(Collectors.toSet());
        }

        return new HashSet<>();

    }

    private Student validateStudent(Long studentId) {
        Student student = this.studentService.findById(studentId);

        if(!Objects.equals(student.getClassId(), null)){
            throw new ConflictException("This student is already allocated to another class");
        }

        return student;
    }
}
