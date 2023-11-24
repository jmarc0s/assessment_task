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
import br.com.jmarcos.assessment_task.service.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;

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

    public Class findById(Long id) {
        return this.classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with the given id"));
    }

    public Class save(ClassRequestDTO classRequest) {
        Class newClass = this.toClass(classRequest);

        return this.classRepository.save(newClass);
    }

    public Class update(ClassRequestDTO classUpdateRequest,
            Long id) {
        Class oldClass = this.findById(id);
        Class toUpdateClass = this.toClass(classUpdateRequest);

        // if (!Objects.equals(oldClass.getName(),
        // toUpdateClass.getName())
        // && this.existsByName(toUpdateClass.getName())) {

        // throw new ConflictException("Class name is already in use");
        // }

        Class updatedClass = fillUpdate(oldClass, toUpdateClass);

        return this.classRepository.save(updatedClass);
    }

    private Class fillUpdate(Class oldClass,
            Class toUpdateClass) {

        oldClass.setTitle(toUpdateClass.getTitle());
        oldClass.setClassStatus(toUpdateClass.getClassStatus());
        oldClass.setClassShift(toUpdateClass.getClassShift());
        oldClass.setMaxStudents(toUpdateClass.getMaxStudents());
        oldClass.setSchoolSegment(toUpdateClass.getSchoolSegment());
        oldClass.setStudents(toUpdateClass.getStudents());
        oldClass.setTeacherHolder(toUpdateClass.getTeacherHolder());

        return oldClass;
    }

    @Transactional
    public void delete(Long id) {
        Class returnedClass = this.findById(id);

        this.updateStudents(returnedClass);

        this.classRepository.delete(returnedClass);
    }

    private void updateStudents(Class returnedClass) {

        for (Student student : returnedClass.getStudents()) {
            student.setClassId(null);
            this.studentService.updateStudent(student);
        }
    }

    private Class toClass(ClassRequestDTO classRequest) {
        Class newClass = new Class();

        newClass.setTitle(classRequest.getTitle());
        newClass.setClassStatus(
                this.validateClassStatus(classRequest.getClassStatus(), classRequest.getTeacherHolder()));
        newClass.setTeacherHolder(this.validateTeacher(classRequest.getTeacherHolder(), classRequest.getClassShift()));
        newClass.setSchoolSegment(classRequest.getSchoolSegment());
        newClass.setMaxStudents(classRequest.getMaxStudents());
        newClass.setClassShift(classRequest.getClassShift());
        newClass.setStudents(this.findStudents(classRequest.getStudentsId()));

        return newClass;
    }

    private ClassStatusEnum validateClassStatus(ClassStatusEnum classStatus, String teacherHolder) {
        if (Objects.equals(teacherHolder, null) && Objects.equals(classStatus, ClassStatusEnum.ACTIVE)) {
            throw new BadRequestException("The class cannot be active if it does not have a teacher");
        }

        return classStatus;
    }

    private String validateTeacher(String teacherHolder, ClassShiftEnum classShift) {
        if (this.classRepository.existsByTeacherHolderAndClassShift(teacherHolder, classShift)) {
            throw new ConflictException("This teacher cannot be assigned to this class this shift");
        }

        return teacherHolder;
    }

    private Set<Student> findStudents(Set<Long> studentsId) {

        if (!Objects.equals(studentsId, null)) {
            return studentsId.stream()
                    .map(studentId -> this.validateStudent(studentId))
                    .collect(Collectors.toSet());
        }

        return new HashSet<>();

    }

    private Student validateStudent(Long studentId) {
        Student student = this.studentService.findById(studentId);

        if (!Objects.equals(student.getClassId(), null)) {
            throw new ConflictException("This student is already allocated to another class");
        }

        return student;
    }
}
