package br.com.jmarcos.assessment_task.service.classes;

import br.com.jmarcos.assessment_task.controller.DTO.classes.ClassRequestDTO;
import br.com.jmarcos.assessment_task.model.Class;
import br.com.jmarcos.assessment_task.model.Student;
import br.com.jmarcos.assessment_task.model.enums.ClassShiftEnum;
import br.com.jmarcos.assessment_task.model.enums.ClassStatusEnum;
import br.com.jmarcos.assessment_task.model.enums.SchoolSegmentEnum;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import br.com.jmarcos.assessment_task.repository.ClassRepository;
import br.com.jmarcos.assessment_task.service.ClassService;
import br.com.jmarcos.assessment_task.service.StudentService;
import br.com.jmarcos.assessment_task.service.exceptions.BadRequestException;
import br.com.jmarcos.assessment_task.service.exceptions.ConflictException;
import br.com.jmarcos.assessment_task.service.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class ClassServiceTest {
    
    @InjectMocks
    private ClassService classService;

    @Mock
    ClassRepository classRepository;

    @Mock
    private StudentService studentService;



    @Test
    void search_returns_AllClasses_WhenSuccessful() {
        PageRequest pageable = PageRequest.of(0, 5);
        List<Class> classList = List.of(this.createClass());
        PageImpl<Class> classPage = new PageImpl<>(classList);
        when(classRepository.findAll(pageable)).thenReturn(classPage);

        Page<Class> all = classService.search(pageable);
        List<Class> returnedCLassList = all.stream().toList();

        Assertions.assertFalse(returnedCLassList.isEmpty());
        Assertions.assertEquals(classList.get(0).getId(), returnedCLassList.get(0).getId());
        Assertions.assertEquals(classList.get(0).getTitle(), returnedCLassList.get(0).getTitle());
        Assertions.assertEquals(classList.get(0).getMaxStudents(), returnedCLassList.get(0).getMaxStudents());
        Assertions.assertEquals(classList.get(0).getTeacherHolder(), returnedCLassList.get(0).getTeacherHolder());
        Assertions.assertEquals(classList.get(0).getClassShift(), returnedCLassList.get(0).getClassShift());
        Assertions.assertEquals(classList.get(0).getClassStatus(), returnedCLassList.get(0).getClassStatus());
        Assertions.assertEquals(classList.get(0).getSchoolSegment(), returnedCLassList.get(0).getSchoolSegment());
        Assertions.assertTrue(returnedCLassList.get(0).getStudents().containsAll(classList.get(0).getStudents()));


        verify(classRepository).findAll(pageable);

    }

    @Test
    void findById_returns_AClass_WhenSuccessful() {
        Class newClass = this.createClass();

        when(classRepository.findById(anyLong()))
                .thenReturn(Optional.of(newClass));

        Class returnedClass = this.classService
                .findById(1L);

        Assertions.assertNotNull(newClass.getTitle(), returnedClass.getTitle());
        Assertions.assertEquals(newClass.getTitle(), returnedClass.getTitle());
        Assertions.assertEquals(newClass.getTeacherHolder(), returnedClass.getTeacherHolder());
        Assertions.assertEquals(newClass.getMaxStudents(), returnedClass.getMaxStudents());
        Assertions.assertEquals(newClass.getClassShift(), returnedClass.getClassShift());
        Assertions.assertEquals(newClass.getClassStatus(), returnedClass.getClassStatus());
        Assertions.assertEquals(newClass.getSchoolSegment(), returnedClass.getSchoolSegment());
        Assertions.assertTrue(newClass.getStudents().containsAll(returnedClass.getStudents()));

        verify(classRepository).findById(anyLong());

    }

    @Test
    void findById_Throws_ResourceNotFoundException_WhenClassNotFound() {

        ResourceNotFoundException resourceNotFoundException = Assertions
                .assertThrows(ResourceNotFoundException.class,
                        () -> classService.findById(anyLong()));

        Assertions.assertTrue(resourceNotFoundException.getMessage()
                .contains("Class not found with the given id"));


        verify(classRepository).findById(anyLong());

    }


    @Test
    void save_Returns_ASavedClas_WhenSuccessful(){
        ClassRequestDTO newClassRequest = this.createClassRequestDTO();
        when(classRepository.save(any(Class.class))).thenReturn(this.createClass());
        when(studentService.findById(anyLong())).thenReturn(this.createStudent());

        Class savedClass = this.classService.save(newClassRequest);

        Assertions.assertNotNull(savedClass.getId());
        Assertions.assertEquals(savedClass.getTitle(), newClassRequest.getTitle());
        Assertions.assertEquals(savedClass.getTeacherHolder(), newClassRequest.getTeacherHolder());
        Assertions.assertEquals(savedClass.getMaxStudents(), newClassRequest.getMaxStudents());
        Assertions.assertEquals(savedClass.getClassShift(), newClassRequest.getClassShift());
        Assertions.assertEquals(savedClass.getClassStatus(), newClassRequest.getClassStatus());
        Assertions.assertEquals(savedClass.getSchoolSegment(), newClassRequest.getSchoolSegment());
        Assertions.assertEquals(savedClass.getStudents()
            .stream()
            .findFirst()
            .get()
            .getId(), newClassRequest.getStudentsId()
            .stream()
            .findFirst().get());

        verify(classRepository).save(any(Class.class));
        verify(classRepository).findAllByTeacherHolder(anyString());
        verify(studentService).findById(anyLong());
        verify(studentService, times(1)).saveSettingClass(any(Student.class));

    }

    @Test
    void save_Throws_BadRequestException_WhenStatusIsInvalid() {
        ClassRequestDTO newClassRequest = this.createClassRequestDTO();
        newClassRequest.setTeacherHolder(null);

        BadRequestException badRequestException = Assertions
                .assertThrows(BadRequestException.class,
                        () -> classService.save(newClassRequest));

        Assertions.assertTrue(badRequestException.getMessage()
                .contains("The class cannot be active if it does not have a teacher"));


        verify(classRepository, times(0)).save(any(Class.class));
        verify(classRepository, times(0)).findAllByTeacherHolder(anyString());
        verify(studentService, times(0)).findById(anyLong());
        verify(studentService, times(0)).saveSettingClass(any(Student.class));

    }

    @Test
    void save_Throws_ConflictException_WhenTeacherIsUnavailable() {
        ClassRequestDTO newClassRequest = this.createClassRequestDTO();
        when(classRepository.findAllByTeacherHolder(anyString())).thenReturn(List.of(this.createClass()));

        ConflictException conflictException = Assertions
                .assertThrows(ConflictException.class,
                        () -> classService.save(newClassRequest));

        Assertions.assertTrue(conflictException.getMessage()
                .contains("This teacher cannot be assigned to this class this shift"));


        verify(classRepository, times(0)).save(any(Class.class));
        verify(classRepository, times(1)).findAllByTeacherHolder(anyString());
        verify(studentService, times(0)).findById(anyLong());
        verify(studentService, times(0)).saveSettingClass(any(Student.class));

    }

    @Test
    void save_Throws_ResourceNotFoundException_WhenStudentNotFound() {
        ClassRequestDTO newClassRequest = this.createClassRequestDTO();
        when(studentService.findById(anyLong())).thenThrow(new ResourceNotFoundException("Student not found with the given id"));

        ResourceNotFoundException resourceNotFoundException = Assertions
                .assertThrows(ResourceNotFoundException.class,
                        () -> classService.save(newClassRequest));

        Assertions.assertTrue(resourceNotFoundException.getMessage()
                .contains("Student not found with the given id"));


        verify(classRepository, times(0)).save(any(Class.class));
        verify(classRepository, times(1)).findAllByTeacherHolder(anyString());
        verify(studentService, times(1)).findById(anyLong());
        verify(studentService, times(0)).saveSettingClass(any(Student.class));

    }


    @Test
    void save_Throws_ConflictException_WhenStudentIsAlreadyAssignedToAnotherClass() {
        ClassRequestDTO newClassRequest = this.createClassRequestDTO();
        Student student = this.createStudent();
        student.setClassId(this.createClass());
         when(studentService.findById(anyLong())).thenReturn(student);

        
        ConflictException conflictException = Assertions
                .assertThrows(ConflictException.class,
                        () -> classService.save(newClassRequest));

        Assertions.assertTrue(conflictException.getMessage()
                .contains("This student is already allocated to another class"));


        verify(classRepository, times(0)).save(any(Class.class));
        verify(classRepository, times(1)).findAllByTeacherHolder(anyString());
        verify(studentService, times(1)).findById(anyLong());
        verify(studentService, times(0)).saveSettingClass(any(Student.class));

    }


    @Test
    void delete_Returns_void_WhenSuccessful() {
        Class classToDelete = this.createClass();
        when(classRepository.findById(anyLong())).thenReturn(Optional.of(classToDelete));

        this.classService.delete(1L);

        verify(classRepository).findById(anyLong());
        verify(classRepository).delete(any(Class.class));
        verify(studentService).updateStudent(any(Student.class));
    }

    @Test
    void delete_Throws_ResourceNotFoundException_WhenClassNotFound() {
        when(classRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions
                .assertThrows(ResourceNotFoundException.class,
                        () -> classService.delete(anyLong()));

            Assertions.assertTrue(resourceNotFoundException.getMessage()
                .contains("Class not found with the given id"));

        verify(classRepository, times(1)).findById(anyLong());
        verify(classRepository, times(0)).delete(any(Class.class));
        verify(studentService, times(0)).updateStudent(any(Student.class));
        
    }

    ClassRequestDTO createClassRequestDTO() {
        ClassRequestDTO newClass = new ClassRequestDTO();

        newClass.setTitle("Turma A");
        newClass.setClassShift(ClassShiftEnum.MORNINGSHIFT);
        newClass.setClassStatus(ClassStatusEnum.ACTIVE);
        newClass.setTeacherHolder("Professor A");
        newClass.setSchoolSegment(SchoolSegmentEnum.FIFTHCHILDISH);
        newClass.setMaxStudents(30);
        newClass.setStudentsId(Set.of(1L));
        
        return newClass;
    }

    Class createClass() {
        Class newClass = new Class();

        newClass.setId(1L);
        newClass.setTitle("Turma A");
        newClass.setClassShift(ClassShiftEnum.MORNINGSHIFT);
        newClass.setClassStatus(ClassStatusEnum.ACTIVE);
        newClass.setTeacherHolder("Professor A");
        newClass.setSchoolSegment(SchoolSegmentEnum.FIFTHCHILDISH);
        newClass.setMaxStudents(30);
        newClass.setStudents(Set.of(this.createStudent()));
        
        return newClass;
    }

    private Student createStudent() {
        Student student = new Student();
        student.setId(1L);
        return student;
    }

}
