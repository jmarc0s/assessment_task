package br.com.jmarcos.assessment_task.controller.student;

import static br.com.jmarcos.assessment_task.model.enums.UserTypeEnum.ROLE_STUDENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.StreamUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.jmarcos.assessment_task.controller.StudentController;
import br.com.jmarcos.assessment_task.controller.DTO.classes.ClassResponseDTO;
import br.com.jmarcos.assessment_task.controller.DTO.student.StudentRequestDTO;
import br.com.jmarcos.assessment_task.controller.DTO.student.StudentResponseDTO;
import br.com.jmarcos.assessment_task.controller.DTO.student.address.AddressRequestDTO;
import br.com.jmarcos.assessment_task.controller.DTO.student.responsible.ResponsibleRequestDTO;
import br.com.jmarcos.assessment_task.controller.DTO.student.responsible.ResponsibleResponseDTO;
import br.com.jmarcos.assessment_task.model.Address;
import br.com.jmarcos.assessment_task.model.Responsible;
import br.com.jmarcos.assessment_task.model.Student;
import br.com.jmarcos.assessment_task.model.User;
import br.com.jmarcos.assessment_task.model.Class;
import br.com.jmarcos.assessment_task.service.StudentService;

@ExtendWith(MockitoExtension.class)
public class StudentControllerTest {

    @InjectMocks
    private StudentController studentController;

    @Mock
    private StudentService studentService;

    private PageRequest pageable = PageRequest.of(0, 5);

    private List<Student> studentList;

    @Test
    void shouldReturnAListOfStudentsWhenSuccessful() {
        when(studentService.search(pageable)).thenReturn(this.createClassPageImpl(List.of(this.createStudent())));

        List<StudentResponseDTO> returnedStudentList = studentController.search(pageable);

        Assertions.assertEquals(studentList.get(0).getId(), returnedStudentList.get(0).getId());
        Assertions.assertEquals(studentList.get(0).getName(), returnedStudentList.get(0).getName());
        Assertions.assertEquals(studentList.get(0).getAddress().getStreet(),
                returnedStudentList.get(0).getAddress().getStreet());
        Assertions.assertEquals(studentList.get(0).getAddress().getNumber(),
                returnedStudentList.get(0).getAddress().getNumber());
        Assertions.assertEquals(studentList.get(0).getAddress().getNeighborhood(),
                returnedStudentList.get(0).getAddress().getNeighborhood());
        Assertions.assertEquals(studentList.get(0).getAddress().getComplement(),
                returnedStudentList.get(0).getAddress().getComplement());
        Assertions.assertEquals(studentList.get(0).getCpf(), returnedStudentList.get(0).getCpf());
        Assertions.assertEquals(studentList.get(0).getClassId(), returnedStudentList.get(0).getClassId());
        Assertions.assertEquals(studentList.get(0).getDateOfBirth(), returnedStudentList.get(0).getDateOfBirth());
        Assertions.assertNotNull(returnedStudentList.get(0).getResponsibles());
        Assertions.assertAll(StreamUtils
                .<Responsible, ResponsibleResponseDTO, Executable>zip(studentList.get(0).getResponsibles().stream(),
                        returnedStudentList.get(0).getResponsibles().stream(), (expected, returned) -> {
                            return () -> {
                                Assertions.assertEquals(expected.getName(), returned.getName());
                                Assertions.assertEquals(expected.getEmail(), returned.getEmail());
                                Assertions.assertEquals(expected.getPhone(), returned.getPhone());

                            };
                        })
                .toArray(Executable[]::new));

        verify(studentService, times(1)).search(pageable);
    }

    @Test
    void shouldReturnAnEmptyListOfStudentsWhenThereAreNoStudents() {
        when(studentService.search(pageable)).thenReturn(this.createClassPageImpl(List.of()));

        List<StudentResponseDTO> returnedStudentList = studentController.search(pageable);

        Assertions.assertTrue(returnedStudentList.isEmpty());

        verify(studentService, times(1)).search(pageable);
    }

    @Test
    void shouldReturnASavedStudentWhenSuccessful() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StudentRequestDTO studentRequest = this.createStudentRequestDTO();
        when(studentService.save(any(StudentRequestDTO.class))).thenReturn(this.createStudent());
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        ResponseEntity<StudentResponseDTO> responseEntitySavedStudent = this.studentController.save(studentRequest,
                uriBuilder);

        Assertions.assertNotNull(responseEntitySavedStudent);
        assertEquals(HttpStatus.CREATED, responseEntitySavedStudent.getStatusCode());
        StudentResponseDTO savedStudent = responseEntitySavedStudent.getBody();

        Assertions.assertNotNull(savedStudent.getId());
        Assertions.assertEquals(studentRequest.getName(), savedStudent.getName());
        Assertions.assertEquals(studentRequest.getCpf(), savedStudent.getCpf());
        Assertions.assertEquals(studentRequest.getAddress().getStreet(), savedStudent.getAddress().getStreet());
        Assertions.assertEquals(studentRequest.getAddress().getNumber(), savedStudent.getAddress().getNumber());
        Assertions.assertEquals(studentRequest.getAddress().getNeighborhood(),
                savedStudent.getAddress().getNeighborhood());
        Assertions.assertEquals(studentRequest.getAddress().getComplement(), savedStudent.getAddress().getComplement());
        Assertions.assertNull(savedStudent.getClassId());
        Assertions.assertEquals(LocalDate.parse(studentRequest.getDateOfBirth(), formatter),
                savedStudent.getDateOfBirth());
        Assertions.assertNotNull(savedStudent.getResponsibles());
        Assertions.assertAll(
                StreamUtils
                        .<ResponsibleRequestDTO, ResponsibleResponseDTO, Executable>zip(
                                studentRequest.getResponsibles().stream(),
                                savedStudent.getResponsibles().stream(), (request, saved) -> {
                                    return () -> {
                                        Assertions.assertEquals(request.getName(), saved.getName());
                                        Assertions.assertEquals(request.getEmail(), saved.getEmail());
                                        Assertions.assertEquals(request.getPhone(), saved.getPhone());

                                    };
                                })
                        .toArray(Executable[]::new));

        verify(studentService, times(1)).save(any(StudentRequestDTO.class));
    }

    @Test
    void shouldThorwsRuntimeExceptionWhenCpfIsAlreadyInUse() {
        StudentRequestDTO studentRequest = this.createStudentRequestDTO();
        when(studentService.save(any(StudentRequestDTO.class)))
                .thenThrow(new RuntimeException("CPF is already in use by someone else"));

        Assertions.assertThrows(RuntimeException.class,
                () -> studentService.save(studentRequest));

        verify(studentService, times(1)).save(any(StudentRequestDTO.class));
    }

    @Test
    void shouldThorwsRuntimeExceptionWhenDateOfBirthIsNotValid() {
        StudentRequestDTO studentRequest = this.createStudentRequestDTO();
        studentRequest.setDateOfBirth("01/10/2100");
        when(studentService.save(any(StudentRequestDTO.class)))
                .thenThrow(new RuntimeException("Invalid date of birth submitted"));

        Assertions.assertThrows(RuntimeException.class,
                () -> studentService.save(studentRequest));

        verify(studentService, times(1)).save(any(StudentRequestDTO.class));
    }

    @Test
    void shouldReturnAStudentByIdWhenSuccessful() {
        Student expectedStudent = this.createStudent();

        when(studentService.findById(anyLong()))
                .thenReturn(expectedStudent);

        ResponseEntity<StudentResponseDTO> responseEntityReturnedStudent = this.studentController
                .searchById(1L);

        Assertions.assertNotNull(responseEntityReturnedStudent);
        assertEquals(HttpStatus.OK, responseEntityReturnedStudent.getStatusCode());
        StudentResponseDTO returnedStudent = responseEntityReturnedStudent.getBody();

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
        Assertions.assertEquals(expectedStudent.getUser().getLogin(), returnedStudent.getCpf());
        Assertions.assertTrue(expectedStudent.getUser().getUserType().contains(ROLE_STUDENT));
        Assertions.assertNotNull(returnedStudent.getResponsibles());
        Assertions.assertAll(StreamUtils
                .<Responsible, ResponsibleResponseDTO, Executable>zip(expectedStudent.getResponsibles().stream(),
                        returnedStudent.getResponsibles().stream(), (expected, returned) -> {
                            return () -> {
                                Assertions.assertEquals(expected.getName(), returned.getName());
                                Assertions.assertEquals(expected.getEmail(), returned.getEmail());
                                Assertions.assertEquals(expected.getPhone(), returned.getPhone());

                            };
                        })
                .toArray(Executable[]::new));

        verify(studentService, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowsRuntimeExceptionWhenStudentNotFound() {
        when(studentService.findById(anyLong()))
                .thenThrow(new RuntimeException("Student not found with the given id"));

        Assertions.assertThrows(RuntimeException.class,
                () -> studentService.findById(anyLong()));

        verify(studentService, times(1)).findById(anyLong());
    }

    @Test
    void shouldNotHaveAnyReturnWheSuccessful() {
        ResponseEntity<Void> responseEntityReturnedClass = studentController.delete(1L);

        Assertions.assertNotNull(responseEntityReturnedClass);
        Assertions.assertEquals(HttpStatus.NO_CONTENT, responseEntityReturnedClass.getStatusCode());
        Assertions.assertNull(responseEntityReturnedClass.getBody());

        verify(studentService, times(1)).delete(anyLong());
    }

    @Test
    void shouldReturnAnUpdatedStudentWhenSuccessful() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StudentRequestDTO studentRequest = this.createStudentRequestDTO();
        Student updatedStudentToReturn = this.createStudent();
        when(studentService.update(any(StudentRequestDTO.class), anyLong())).thenReturn(updatedStudentToReturn);

        ResponseEntity<StudentResponseDTO> responseEntityUpdatedStudent = this.studentController.update(studentRequest,
                2L);

        Assertions.assertNotNull(responseEntityUpdatedStudent);
        assertEquals(HttpStatus.OK, responseEntityUpdatedStudent.getStatusCode());
        StudentResponseDTO updatedStudent = responseEntityUpdatedStudent.getBody();

        Assertions.assertNotNull(updatedStudent.getId());
        Assertions.assertEquals(studentRequest.getName(), updatedStudent.getName());
        Assertions.assertEquals(studentRequest.getCpf(), updatedStudent.getCpf());
        Assertions.assertEquals(studentRequest.getAddress().getStreet(), updatedStudent.getAddress().getStreet());
        Assertions.assertEquals(studentRequest.getAddress().getNumber(), updatedStudent.getAddress().getNumber());
        Assertions.assertEquals(studentRequest.getAddress().getNeighborhood(),
                updatedStudent.getAddress().getNeighborhood());
        Assertions.assertEquals(studentRequest.getAddress().getComplement(),
                updatedStudent.getAddress().getComplement());
        Assertions.assertNull(updatedStudent.getClassId());
        Assertions.assertEquals(LocalDate.parse(studentRequest.getDateOfBirth(), formatter),
                updatedStudent.getDateOfBirth());
        Assertions.assertNotNull(updatedStudent.getResponsibles());
        Assertions.assertAll(StreamUtils
                .<ResponsibleRequestDTO, ResponsibleResponseDTO, Executable>zip(
                        studentRequest.getResponsibles().stream(),
                        updatedStudent.getResponsibles().stream(), (request, saved) -> {
                            return () -> {
                                Assertions.assertEquals(request.getName(), saved.getName());
                                Assertions.assertEquals(request.getEmail(), saved.getEmail());
                                Assertions.assertEquals(request.getPhone(), saved.getPhone());

                            };
                        })
                .toArray(Executable[]::new));

        verify(studentService, times(1)).update(any(StudentRequestDTO.class), anyLong());
    }

    @Test
    void shouldThorwsRuntimeExceptionWhenCpfIsAlreadyInUseOnUpdate() {
        StudentRequestDTO studentRequest = this.createStudentRequestDTO();
        when(studentService.update(any(StudentRequestDTO.class), anyLong()))
                .thenThrow(new RuntimeException("This cpf is already in use by someone else"));

        Assertions.assertThrows(RuntimeException.class,
                () -> studentService.update(studentRequest, 2L));

        verify(studentService, times(1)).update(any(StudentRequestDTO.class), anyLong());
    }

    @Test
    void shouldThorwsRuntimeExceptionWhenDateOfBirthIsNotValidOnUpdate() {
        StudentRequestDTO studentRequest = this.createStudentRequestDTO();
        studentRequest.setDateOfBirth("01/10/2100");
        when(studentService.update(any(StudentRequestDTO.class), anyLong()))
                .thenThrow(new RuntimeException("Invalid date of birth submitted"));

        Assertions.assertThrows(RuntimeException.class,
                () -> studentService.update(studentRequest, 2L));

        verify(studentService, times(1)).update(any(StudentRequestDTO.class), anyLong());
    }

    @Test
    void shouldThorwsRuntimeExceptionWhenStudentNotFoundOnUpdate() {
        StudentRequestDTO studentRequest = this.createStudentRequestDTO();
        when(studentService.update(any(StudentRequestDTO.class), anyLong()))
                .thenThrow(new RuntimeException("Student not found with the given id"));

        Assertions.assertThrows(RuntimeException.class,
                () -> studentService.update(studentRequest, 5L));

        verify(studentService, times(1)).update(any(StudentRequestDTO.class), anyLong());
    }

    @Test
    void shouldReturnToTheClassAssociatedWithThisStudent() {
        User user = new User();
        Class expectedClass = this.createClass();
        when(studentService.findMyClass(any(User.class))).thenReturn(expectedClass);

        ResponseEntity<ClassResponseDTO> responseEntityReturnedClass = this.studentController.getClass(user);

        Assertions.assertNotNull(responseEntityReturnedClass);
        assertEquals(HttpStatus.OK, responseEntityReturnedClass.getStatusCode());
        ClassResponseDTO returnedClass = responseEntityReturnedClass.getBody();

        Assertions.assertNotNull(returnedClass);
        Assertions.assertEquals(expectedClass.getId(), returnedClass.getId());
        Assertions.assertEquals(expectedClass.getTitle(), returnedClass.getTitle());
        Assertions.assertNull(returnedClass.getTeacherHolder());
        Assertions.assertNull(returnedClass.getClassShift());
        Assertions.assertNull(returnedClass.getClassStatus());
        Assertions.assertEquals(0, returnedClass.getMaxStudents());
        Assertions.assertNull(returnedClass.getSchoolSegment());
        Assertions.assertNotNull(returnedClass.getStudentsId());
        Assertions.assertTrue(returnedClass.getStudentsId().isEmpty());

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
        student.setUser(new User(1L, "807.292.180-07", "student1234", Set.of(ROLE_STUDENT)));
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
        student.setUser(new User(2L, "274.996.840-24", "1234student", Set.of(ROLE_STUDENT)));

        return student;
    }

    Class createClass() {
        Class newClass = new Class();

        newClass.setId(1L);
        newClass.setTitle("Turma A");
        newClass.setStudents(Set.of());

        return newClass;
    }

    private PageImpl<Student> createClassPageImpl(List<Student> list) {
        studentList = list;
        PageImpl<Student> classPage = new PageImpl<>(studentList);

        return classPage;
    }
}
