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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import br.com.jmarcos.assessment_task.service.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @InjectMocks
    private StudentService studentService;

    @Mock
    StudentRepository studentRepository;

    @Test
    void shouldReturnAPageOfStudentsWhenSuccessful() {
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
        Assertions.assertTrue(
                returnedStudentList.get(0).getResponsibles().containsAll(studentList.get(0).getResponsibles()));
        verify(studentRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldReturnAnEmptyPageOfStudentsWhenThereAreNoStudents() {
        PageRequest pageable = PageRequest.of(0, 5);
        List<Student> studentList = List.of();
        PageImpl<Student> studentPage = new PageImpl<>(studentList);
        when(studentRepository.findAll(pageable)).thenReturn(studentPage);

        Page<Student> all = studentService.search(pageable);
        List<Student> returnedStudentList = all.stream().toList();

        Assertions.assertTrue(returnedStudentList.isEmpty());
        assertIterableEquals(studentList, returnedStudentList);

        verify(studentRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldReturnAStudentByIdWhenSuccessful() {
        Student expectedStudent = this.createStudent();

        when(studentRepository.findById(anyLong()))
                .thenReturn(Optional.of(expectedStudent));

        Student returnedStudent = this.studentService
                .findById(1L);

        Assertions.assertEquals(expectedStudent.getId(), returnedStudent.getId());
        Assertions.assertEquals(expectedStudent.getName(), returnedStudent.getName());
        Assertions.assertEquals(expectedStudent.getCpf(), returnedStudent.getCpf());
        Assertions.assertNotNull(expectedStudent.getAddress().getId());
        Assertions.assertEquals(expectedStudent.getAddress().getStreet(), returnedStudent.getAddress().getStreet());
        Assertions.assertEquals(expectedStudent.getAddress().getNumber(), returnedStudent.getAddress().getNumber());
        Assertions.assertEquals(expectedStudent.getAddress().getNeighborhood(),
                returnedStudent.getAddress().getNeighborhood());
        Assertions.assertEquals(expectedStudent.getAddress().getComplement(),
                returnedStudent.getAddress().getComplement());
        Assertions.assertNull(returnedStudent.getClassId());
        Assertions.assertEquals(expectedStudent.getDateOfBirth(), returnedStudent.getDateOfBirth());
        Assertions.assertNotNull(returnedStudent.getUser().getId());
        Assertions.assertEquals(expectedStudent.getUser().getLogin(), returnedStudent.getCpf());
        Assertions.assertEquals(expectedStudent.getUser().getPassword(), returnedStudent.getUser().getPassword());
        Assertions.assertTrue(expectedStudent.getUser().getUserType().contains(UserTypeEnum.ROLE_STUDENT));

        Assertions.assertAll(
                StreamUtils.<Responsible, Responsible, Executable>zip(expectedStudent.getResponsibles().stream(),
                        returnedStudent.getResponsibles().stream(), (expected, returned) -> {
                            return () -> {
                                Assertions.assertEquals(expected.getName(), returned.getName());
                                Assertions.assertEquals(expected.getEmail(), returned.getEmail());
                                Assertions.assertEquals(expected.getPhone(), returned.getPhone());

                            };
                        }).toArray(Executable[]::new));

        verify(studentRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowsRuntimeExceptionWhenStudentNotFound() {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class,
                () -> studentService.findById(anyLong()));

        verify(studentRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldReturnASavedStudentWhenSuccessful() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StudentRequestDTO studentRequest = this.createStudentRequestDTO();
        when(studentRepository.save(any(Student.class))).thenReturn(this.createStudent());

        Student savedStudent = this.studentService.save(studentRequest);

        Assertions.assertNotNull(savedStudent.getId());
        Assertions.assertEquals(studentRequest.getName(), savedStudent.getName());
        Assertions.assertEquals(studentRequest.getCpf(), savedStudent.getCpf());
        Assertions.assertNotNull(savedStudent.getAddress().getId());
        Assertions.assertEquals(studentRequest.getAddress().getStreet(), savedStudent.getAddress().getStreet());
        Assertions.assertEquals(studentRequest.getAddress().getNumber(), savedStudent.getAddress().getNumber());
        Assertions.assertEquals(studentRequest.getAddress().getNeighborhood(),
                savedStudent.getAddress().getNeighborhood());
        Assertions.assertEquals(studentRequest.getAddress().getComplement(), savedStudent.getAddress().getComplement());
        Assertions.assertNull(savedStudent.getClassId());
        Assertions.assertEquals(LocalDate.parse(studentRequest.getDateOfBirth(), formatter),
                savedStudent.getDateOfBirth());
        Assertions.assertNotNull(savedStudent.getUser().getId());
        Assertions.assertEquals(studentRequest.getCpf(), savedStudent.getUser().getLogin());
        Assertions.assertTrue(new BCryptPasswordEncoder().matches(studentRequest.getPassword(),
                savedStudent.getUser().getPassword()));
        Assertions.assertTrue(savedStudent.getUser().getUserType().contains(UserTypeEnum.ROLE_STUDENT));
        Assertions.assertAll(
                StreamUtils
                        .<ResponsibleRequestDTO, Responsible, Executable>zip(studentRequest.getResponsibles().stream(),
                                savedStudent.getResponsibles().stream(), (request, saved) -> {
                                    return () -> {
                                        Assertions.assertEquals(request.getName(), saved.getName());
                                        Assertions.assertEquals(request.getEmail(), saved.getEmail());
                                        Assertions.assertEquals(request.getPhone(), saved.getPhone());

                                    };
                                })
                        .toArray(Executable[]::new));

        verify(studentRepository).save(any(Student.class));
        verify(studentRepository).existsByCpf(anyString());
    }

    @Test
    void shouldThorwsRuntimeExceptionWhenCpfIsAlreadyInUse() {
        StudentRequestDTO studentRequest = this.createStudentRequestDTO();
        when(studentRepository.existsByCpf(anyString())).thenReturn(true);

        Assertions.assertThrows(RuntimeException.class,
                () -> studentService.save(studentRequest));

        verify(studentRepository, times(0)).save(any(Student.class));
        verify(studentRepository, times(1)).existsByCpf(anyString());
    }

    @Test
    void shouldThorwsRuntimeExceptionWhenDateOfBirthIsNotValid() {
        StudentRequestDTO studentRequest = this.createStudentRequestDTO();
        studentRequest.setDateOfBirth("01/10/2100");

        Assertions.assertThrows(RuntimeException.class,
                () -> studentService.save(studentRequest));

        verify(studentRepository, times(0)).save(any(Student.class));
        verify(studentRepository, times(1)).existsByCpf(anyString());
    }

    @Test
    void shouldNotHaveAnyReturnWheSuccessful() {
        Student studentToDelete = this.createStudent();
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(studentToDelete));

        this.studentService.delete(1L);

        verify(studentRepository, times(1)).findById(anyLong());
        verify(studentRepository, times(1)).delete(any(Student.class));
    }

    @Test
    void shouldThrowsRuntimeExceptionWhenStudentNotFoundOnDelete() {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class,
                () -> studentService.delete(anyLong()));

        verify(studentRepository, times(1)).findById(anyLong());
        verify(studentRepository, times(0)).delete(any(Student.class));
    }

    @Test
    void shouldReturnAnUpdatedStudentWhenSuccessful() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StudentRequestDTO studentRequest = this.createStudentRequestDTO();
        Student studentToBeUpdated = this.createStudentToBeUpdated();
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(studentToBeUpdated));
        when(studentRepository.save(any(Student.class))).thenReturn(studentToBeUpdated);

        Student updatedStudent = this.studentService.update(studentRequest, 2L);

        Assertions.assertNotNull(updatedStudent.getId());
        Assertions.assertEquals(studentRequest.getName(), updatedStudent.getName());
        Assertions.assertEquals(studentRequest.getCpf(), updatedStudent.getCpf());
        Assertions.assertNotNull(updatedStudent.getAddress().getId());
        Assertions.assertEquals(studentRequest.getAddress().getStreet(), updatedStudent.getAddress().getStreet());
        Assertions.assertEquals(studentRequest.getAddress().getNumber(), updatedStudent.getAddress().getNumber());
        Assertions.assertEquals(studentRequest.getAddress().getNeighborhood(),
                updatedStudent.getAddress().getNeighborhood());
        Assertions.assertEquals(studentRequest.getAddress().getComplement(),
                updatedStudent.getAddress().getComplement());
        Assertions.assertNull(updatedStudent.getClassId());
        Assertions.assertEquals(LocalDate.parse(studentRequest.getDateOfBirth(), formatter),
                updatedStudent.getDateOfBirth());
        Assertions.assertNotNull(updatedStudent.getUser().getId());
        Assertions.assertEquals(studentRequest.getCpf(), updatedStudent.getUser().getLogin());
        // FIXME
        Assertions.assertEquals(studentRequest.getPassword(), updatedStudent.getUser().getPassword());
        Assertions.assertTrue(updatedStudent.getUser().getUserType().contains(UserTypeEnum.ROLE_STUDENT));
        Assertions.assertAll(StreamUtils
                .<ResponsibleRequestDTO, Responsible, Executable>zip(studentRequest.getResponsibles().stream(),
                        updatedStudent.getResponsibles().stream(), (request, saved) -> {
                            return () -> {
                                Assertions.assertEquals(request.getName(), saved.getName());
                                Assertions.assertEquals(request.getEmail(), saved.getEmail());
                                Assertions.assertEquals(request.getPhone(), saved.getPhone());

                            };
                        })
                .toArray(Executable[]::new));

        verify(studentRepository, times(1)).save(any(Student.class));
        verify(studentRepository, times(1)).findByCpf(anyString());
        verify(studentRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThorwsRuntimeExceptionWhenCpfIsAlreadyInUseOnUpdate() {
        StudentRequestDTO studentRequest = this.createStudentRequestDTO();
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(this.createStudentToBeUpdated()));
        when(studentRepository.findByCpf(anyString())).thenReturn(Optional.of(this.createStudent()));

        Assertions.assertThrows(RuntimeException.class,
                () -> studentService.update(studentRequest, 2L));

        verify(studentRepository, times(0)).save(any(Student.class));
        verify(studentRepository, times(1)).findByCpf(anyString());
        verify(studentRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThorwsRuntimeExceptionWhenDateOfBirthIsNotValidOnUpdate() {
        StudentRequestDTO studentRequest = this.createStudentRequestDTO();
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(this.createStudentToBeUpdated()));
        studentRequest.setDateOfBirth("01/10/2100");

        Assertions.assertThrows(RuntimeException.class,
                () -> studentService.update(studentRequest, 2L));

        verify(studentRepository, times(0)).save(any(Student.class));
        verify(studentRepository, times(1)).findByCpf(anyString());
        verify(studentRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThorwsRuntimeExceptionWhenStudentNotFoundOnUpdate() {
        StudentRequestDTO studentRequest = this.createStudentRequestDTO();
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> studentService.update(studentRequest, 2L));

        verify(studentRepository, times(0)).save(any(Student.class));
        verify(studentRepository, times(0)).findByCpf(anyString());
        verify(studentRepository, times(1)).findById(anyLong());
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
        student.setUser(new User(1L, "807.292.180-07", new BCryptPasswordEncoder().encode("student1234"),
                Set.of(UserTypeEnum.ROLE_STUDENT)));
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
