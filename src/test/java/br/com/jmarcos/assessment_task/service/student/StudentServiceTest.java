package br.com.jmarcos.assessment_task.service.student;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import org.springframework.data.util.StreamUtils;

import org.junit.jupiter.api.function.Executable;

import br.com.jmarcos.assessment_task.controller.DTO.student.StudentRequestDTO;
import br.com.jmarcos.assessment_task.controller.DTO.student.address.AddressRequestDTO;
import br.com.jmarcos.assessment_task.controller.DTO.student.responsible.ResponsibleRequestDTO;
import br.com.jmarcos.assessment_task.model.Address;
import br.com.jmarcos.assessment_task.model.Responsible;
import br.com.jmarcos.assessment_task.model.Student;
import br.com.jmarcos.assessment_task.model.User;
import br.com.jmarcos.assessment_task.model.enums.UserTypeEnum;
import br.com.jmarcos.assessment_task.repository.StudentRepository;
import br.com.jmarcos.assessment_task.service.StudentService;
import br.com.jmarcos.assessment_task.service.exceptions.BadRequestException;
import br.com.jmarcos.assessment_task.service.exceptions.ConflictException;
import br.com.jmarcos.assessment_task.service.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @InjectMocks
    private StudentService studentService;

    @Mock
    StudentRepository studentRepository;

    @Test
    void search_returns_AllClasses_WhenSuccessful() {
        PageRequest pageable = PageRequest.of(0, 5);
        List<Student> studentList = List.of(this.createStudent());
        PageImpl<Student> studentPage = new PageImpl<>(studentList);
        when(studentRepository.findAll(pageable)).thenReturn(studentPage);

        Page<Student> all = studentService.search(pageable);
        List<Student> returnedStudentList = all.stream().toList();

        assertIterableEquals(studentList, returnedStudentList);
        Assertions.assertEquals(studentList.get(0).getId(), returnedStudentList.get(0).getId());
        Assertions.assertEquals(studentList.get(0).getName(), returnedStudentList.get(0).getName());
        Assertions.assertEquals(studentList.get(0).getAddress(), returnedStudentList.get(0).getAddress());
        Assertions.assertEquals(studentList.get(0).getCpf(), returnedStudentList.get(0).getCpf());
        Assertions.assertEquals(studentList.get(0).getClassId(), returnedStudentList.get(0).getClassId());
        Assertions.assertEquals(studentList.get(0).getDateOfBirth(), returnedStudentList.get(0).getDateOfBirth());
        Assertions.assertEquals(studentList.get(0).getResponsibles(), returnedStudentList.get(0).getResponsibles());
        Assertions.assertTrue(returnedStudentList.get(0).getResponsibles().containsAll(studentList.get(0).getResponsibles()));
        verify(studentRepository).findAll(pageable);

    }

    @Test
    void findById_returns_AStudent_WhenSuccessful() {
        Student student = this.createStudent();

        when(studentRepository.findById(anyLong()))
                .thenReturn(Optional.of(student));

        Student returnedStudent = this.studentService
                .findById(1L);

        Assertions.assertEquals( student.getId(), returnedStudent.getId());
        Assertions.assertEquals(student.getName(), returnedStudent.getName());
        Assertions.assertEquals(student.getCpf(), returnedStudent.getCpf());
        Assertions.assertNotNull(student.getAddress().getId());
        Assertions.assertEquals(student.getAddress().getStreet(), returnedStudent.getAddress().getStreet());
        Assertions.assertEquals(student.getAddress().getNumber(), returnedStudent.getAddress().getNumber());
        Assertions.assertEquals(student.getAddress().getNeighborhood(), returnedStudent.getAddress().getNeighborhood());
        Assertions.assertEquals(student.getAddress().getComplement(), returnedStudent.getAddress().getComplement());
        Assertions.assertNull(student.getClassId());
        Assertions.assertEquals(student.getDateOfBirth(), returnedStudent.getDateOfBirth());
        Assertions.assertNotNull(student.getUser().getId());
        Assertions.assertEquals(student.getUser().getLogin(), returnedStudent.getCpf());
        Assertions.assertEquals(student.getUser().getPassword(), returnedStudent.getUser().getPassword());
        Assertions.assertTrue(student.getUser().getUserType().contains(UserTypeEnum.ROLE_STUDENT));
        
        Assertions.assertAll( StreamUtils.<Responsible, Responsible, Executable>zip(student.getResponsibles().stream(),  returnedStudent.getResponsibles().stream(), (saved, request) -> {
        return () -> {
            Assertions.assertEquals(saved.getName(), request.getName());
            Assertions.assertEquals(saved.getEmail(), request.getEmail());
            Assertions.assertEquals(saved.getPhone(), request.getPhone());
        
        };
    }).toArray(Executable[]::new)
);


        verify(studentRepository).findById(anyLong());

    }



    @Test
    void save_Returns_ASavedStudent_WehnSuccessful(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StudentRequestDTO studentRequest = this.createStudentRequestDTO();
        when(studentRepository.save(any(Student.class))).thenReturn(this.createStudent());
        
        Student savedStudent = this.studentService.save(studentRequest);

        Assertions.assertNotNull(savedStudent.getId());
        Assertions.assertEquals(savedStudent.getName(), studentRequest.getName());
        Assertions.assertEquals(savedStudent.getCpf(), studentRequest.getCpf());
        Assertions.assertNotNull(savedStudent.getAddress().getId());
        Assertions.assertEquals(savedStudent.getAddress().getStreet(), studentRequest.getAddress().getStreet());
        Assertions.assertEquals(savedStudent.getAddress().getNumber(), studentRequest.getAddress().getNumber());
        Assertions.assertEquals(savedStudent.getAddress().getNeighborhood(), studentRequest.getAddress().getNeighborhood());
        Assertions.assertEquals(savedStudent.getAddress().getComplement(), studentRequest.getAddress().getComplement());
        Assertions.assertNull(savedStudent.getClassId());
        Assertions.assertEquals(savedStudent.getDateOfBirth(), LocalDate.parse(studentRequest.getDateOfBirth(), formatter));
        Assertions.assertNotNull(savedStudent.getUser().getId());
        Assertions.assertEquals(savedStudent.getUser().getLogin(), studentRequest.getCpf());
        Assertions.assertEquals(savedStudent.getUser().getPassword(), studentRequest.getPassword());
        Assertions.assertTrue(savedStudent.getUser().getUserType().contains(UserTypeEnum.ROLE_STUDENT));
        Assertions.assertAll( StreamUtils.<Responsible, ResponsibleRequestDTO, Executable>zip(savedStudent.getResponsibles().stream(),  studentRequest.getResponsibles().stream(), (saved, request) -> {
        return () -> {
            Assertions.assertEquals(saved.getName(), request.getName());
            Assertions.assertEquals(saved.getEmail(), request.getEmail());
            Assertions.assertEquals(saved.getPhone(), request.getPhone());
        
        };
    }).toArray(Executable[]::new)
);

        verify(studentRepository).save(any(Student.class));
        verify(studentRepository).existsByCpf(anyString());
    }

    @Test
    void save_Throws_ConflictException_WhenCpfIsAlreadyInUse() {
        StudentRequestDTO studentRequest = this.createStudentRequestDTO();
        when(studentRepository.existsByCpf(anyString())).thenReturn(true);

        ConflictException conflictException = Assertions
                .assertThrows(ConflictException.class,
                        () -> studentService.save(studentRequest));

        Assertions.assertTrue(conflictException.getMessage()
                .contains("CPF is already in use by someone else"));

        verify(studentRepository, times(0)).save(any(Student.class));
        verify(studentRepository).existsByCpf(anyString());

    }


    @Test
    void save_Throws_BadRequestException_WhenDateOfBirthIsNotValid() {
        StudentRequestDTO studentRequest = this.createStudentRequestDTO();
        studentRequest.setDateOfBirth("01/10/2100");
        
        BadRequestException badRequestException = Assertions
                .assertThrows(BadRequestException.class,
                        () -> studentService.save(studentRequest));

        Assertions.assertTrue(badRequestException.getMessage()
                .contains("Invalid date of birth submitted"));

        verify(studentRepository, times(0)).save(any(Student.class));
        verify(studentRepository).existsByCpf(anyString());

    }

    @Test
    void delete_Returns_Void_WhenSuccessful() {
        Student studentToDelete = this.createStudent();
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(studentToDelete));

        this.studentService.delete(1L);

        verify(studentRepository).findById(anyLong());
        verify(studentRepository).delete(any(Student.class));
    }


    @Test
    void delete_Throws_ResourceNotFoundException_WhenStudentNotFound() {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = Assertions
                .assertThrows(ResourceNotFoundException.class,
                        () -> studentService.delete(anyLong()));

            Assertions.assertTrue(resourceNotFoundException.getMessage()
                .contains("Student not found with the given id"));

        verify(studentRepository, times(1)).findById(anyLong());
        verify(studentRepository, times(0)).delete(any(Student.class));
        
    }

    @Test
    void update_Returns_AnUpdatedStudent_WehnSuccessful(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StudentRequestDTO studentRequest = this.createStudentRequestDTO();
        Student studentToBeUpdated = this.createStudentToBeUpdated();
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(studentToBeUpdated));
        when(studentRepository.save(any(Student.class))).thenReturn(studentToBeUpdated);

        Student updatedStudent = this.studentService.update(studentRequest, 2L);

        Assertions.assertNotNull(updatedStudent.getId());
        Assertions.assertEquals(updatedStudent.getName(), studentRequest.getName());
        Assertions.assertEquals(updatedStudent.getCpf(), studentRequest.getCpf());
        Assertions.assertNotNull(updatedStudent.getAddress().getId());
        Assertions.assertEquals(updatedStudent.getAddress().getStreet(), studentRequest.getAddress().getStreet());
        Assertions.assertEquals(updatedStudent.getAddress().getNumber(), studentRequest.getAddress().getNumber());
        Assertions.assertEquals(updatedStudent.getAddress().getNeighborhood(), studentRequest.getAddress().getNeighborhood());
        Assertions.assertEquals(updatedStudent.getAddress().getComplement(), studentRequest.getAddress().getComplement());
        Assertions.assertNull(updatedStudent.getClassId());
        Assertions.assertEquals(updatedStudent.getDateOfBirth(), LocalDate.parse(studentRequest.getDateOfBirth(), formatter));
        Assertions.assertNotNull(updatedStudent.getUser().getId());
        Assertions.assertEquals(updatedStudent.getUser().getLogin(), studentRequest.getCpf());
        Assertions.assertEquals(updatedStudent.getUser().getPassword(), studentRequest.getPassword());
        Assertions.assertTrue(updatedStudent.getUser().getUserType().contains(UserTypeEnum.ROLE_STUDENT));
        Assertions.assertAll( StreamUtils.<Responsible, ResponsibleRequestDTO, Executable>zip(updatedStudent.getResponsibles().stream(),  studentRequest.getResponsibles().stream(), (saved, request) -> {
        return () -> {
            Assertions.assertEquals(saved.getName(), request.getName());
            Assertions.assertEquals(saved.getEmail(), request.getEmail());
            Assertions.assertEquals(saved.getPhone(), request.getPhone());
        
        };
    }).toArray(Executable[]::new)
);

        verify(studentRepository).save(any(Student.class));
        verify(studentRepository).existsByCpf(anyString());
    }

    @Test
    void update_Throws_ConflictException_WhenCpfIsAlreadyInUse() {
        StudentRequestDTO studentRequest = this.createStudentRequestDTO();
        when(studentRepository.existsByCpf(anyString())).thenReturn(true);

        ConflictException conflictException = Assertions
                .assertThrows(ConflictException.class,
                        () -> studentService.save(studentRequest));

        Assertions.assertTrue(conflictException.getMessage()
                .contains("CPF is already in use by someone else"));

        verify(studentRepository, times(0)).save(any(Student.class));
        verify(studentRepository).existsByCpf(anyString());

    }


    @Test
    void update_Throws_BadRequestException_WhenDateOfBirthIsNotValid() {
        StudentRequestDTO studentRequest = this.createStudentRequestDTO();
        studentRequest.setDateOfBirth("01/10/2100");
        
        BadRequestException badRequestException = Assertions
                .assertThrows(BadRequestException.class,
                        () -> studentService.save(studentRequest));

        Assertions.assertTrue(badRequestException.getMessage()
                .contains("Invalid date of birth submitted"));

        verify(studentRepository, times(0)).save(any(Student.class));
        verify(studentRepository).existsByCpf(anyString());

    }

    StudentRequestDTO createStudentRequestDTO() {
        StudentRequestDTO student = new StudentRequestDTO();

        student.setName("Aluno A");
        student.setAddress(this.createAddressRequestDTO());
        student.setCpf("807.292.180-07");
        student.setDateOfBirth("22/02/2000");
        student.setPassword("student1234");
        student.setResponsibles(Set.of(this.createResponsibleRequestDTO()));
        
        return student;
    }

    private ResponsibleRequestDTO createResponsibleRequestDTO() {
        ResponsibleRequestDTO responsible = new ResponsibleRequestDTO();

        responsible.setName("Mãe");
        responsible.setEmail("mae@gmail.com");
        responsible.setPhone("85 9999-9999");

        return responsible;
    }

    private AddressRequestDTO createAddressRequestDTO() {
        AddressRequestDTO address = new AddressRequestDTO();

        address.setStreet("Rua A");
        address.setNumber(22);
        address.setNeighborhood("Vizinhança");
        address.setComplement("perto dali");

        return address;
    }

    Student createStudent() {
        Student student = new Student();

        student.setId(1L);
        student.setName("Aluno A");
        student.setCpf("807.292.180-07");
        student.setDateOfBirth(LocalDate.of(2000, 02, 22));
        student.setUser(new User(1L, "807.292.180-07", "student1234", Set.of(UserTypeEnum.ROLE_STUDENT)));
        student.setResponsibles(Set.of(new Responsible(1L, "Mãe", "mae@gmail.com", "85 9999-9999")));
        student.setAddress(new Address(1L, "Rua A", 22, "Vizinhança", "perto dali"));
        
        return student;
    }

    Student createStudentToBeUpdated() {
        Student student = new Student();

        student.setId(2L);
        student.setName("Aluno B");
        student.setAddress(new Address(2L, "Rua B", 33, "Vizinhançaaa", "perto dacula"));
        student.setCpf("274.996.840-24");
        student.setDateOfBirth(LocalDate.of(2000, 03, 10));
        student.setResponsibles(Set.of(new Responsible(1L, "Pai", "pai@gmail.com", "85 8888-8888")));
        student.setUser(new User(2L, "274.996.840-24", "1234student", Set.of(UserTypeEnum.ROLE_STUDENT)));

        return student;
    }

}
    

