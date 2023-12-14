package br.com.jmarcos.assessment_task.controller.classes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.StreamUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.jmarcos.assessment_task.controller.ClassController;
import br.com.jmarcos.assessment_task.controller.DTO.classes.ClassRequestDTO;
import br.com.jmarcos.assessment_task.controller.DTO.classes.ClassResponseDTO;
import br.com.jmarcos.assessment_task.controller.DTO.student.responsible.ResponsibleRequestDTO;
import br.com.jmarcos.assessment_task.model.Student;
import br.com.jmarcos.assessment_task.model.Class;
import br.com.jmarcos.assessment_task.model.Responsible;
import br.com.jmarcos.assessment_task.model.enums.ClassShiftEnum;
import br.com.jmarcos.assessment_task.model.enums.ClassStatusEnum;
import br.com.jmarcos.assessment_task.model.enums.SchoolSegmentEnum;
import br.com.jmarcos.assessment_task.service.ClassService;
import br.com.jmarcos.assessment_task.service.exceptions.BadRequestException;
import br.com.jmarcos.assessment_task.service.exceptions.ConflictException;
import br.com.jmarcos.assessment_task.service.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class ClassControllerTest {

        @InjectMocks
        private ClassController classController;

        @Mock
        private ClassService classService;


        @Test
        void shouldReturnAListOfClassesWhenSuccessful() {
                PageRequest pageable = PageRequest.of(0, 5);
                List<Class> classList = List.of(this.createClass());
                PageImpl<Class> classPage = new PageImpl<>(classList);
                when(classService.search(pageable)).thenReturn(classPage);

                List<ClassResponseDTO> returnedClassList = classController.search(pageable);

                Assertions.assertFalse(returnedClassList.isEmpty());
                Assertions.assertEquals(classList.size(), returnedClassList.size());
                Assertions.assertEquals(classList.get(0).getId(), returnedClassList.get(0).getId());
                Assertions.assertEquals(classList.get(0).getTitle(), returnedClassList.get(0).getTitle());
                Assertions.assertEquals(classList.get(0).getMaxStudents(), returnedClassList.get(0).getMaxStudents());
                Assertions.assertEquals(classList.get(0).getTeacherHolder(),
                                returnedClassList.get(0).getTeacherHolder());
                Assertions.assertEquals(classList.get(0).getClassShift(), returnedClassList.get(0).getClassShift());
                Assertions.assertEquals(classList.get(0).getClassStatus(), returnedClassList.get(0).getClassStatus());
                Assertions.assertEquals(classList.get(0).getSchoolSegment(),
                                returnedClassList.get(0).getSchoolSegment());
                Assertions.assertTrue(extractStudentsIdFromStudentSet(classList.get(0)).containsAll(returnedClassList.get(0).getStudentsId()));

                verify(classService, times(1)).search(pageable);
        }

        @Test
        void shouldReturnAnEmptyListOfClassesWhenThereAreNoClasses() {
                PageRequest pageable = PageRequest.of(0, 5);
                List<Class> classList = List.of();
                PageImpl<Class> classPage = new PageImpl<>(classList);
                when(classService.search(pageable)).thenReturn(classPage);

                List<ClassResponseDTO> returnedStudentList = classController.search(pageable);

                Assertions.assertTrue(returnedStudentList.isEmpty());
                assertIterableEquals(classList, returnedStudentList);

                verify(classService, times(1)).search(pageable);
        }
   
        @Test
        void shouldReturnASavedClassWhenSuccessful(){
                ClassRequestDTO classRequestDTO = createClassRequestDTO();
                Class newClass = createClass();
                when(classService.save(any(ClassRequestDTO.class))).thenReturn(newClass);
                UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");


                ResponseEntity<ClassResponseDTO> responseEntitySavedClass = classController.save(classRequestDTO, uriBuilder);


                Assertions.assertNotNull(responseEntitySavedClass);
                assertEquals(HttpStatus.CREATED, responseEntitySavedClass.getStatusCode());
                ClassResponseDTO savedClass = responseEntitySavedClass.getBody();

                URI location = responseEntitySavedClass.getHeaders().getLocation();
                Assertions.assertNotNull(location);
                Assertions.assertEquals("/classes/" + savedClass.getId(), location.getPath());

                Assertions.assertNotNull(savedClass.getId());
                Assertions.assertEquals(classRequestDTO.getTitle(), savedClass.getTitle());
                Assertions.assertEquals(classRequestDTO.getClassShift(), savedClass.getClassShift());
                Assertions.assertEquals(classRequestDTO.getClassStatus(), savedClass.getClassStatus());
                Assertions.assertEquals(classRequestDTO.getMaxStudents(), savedClass.getMaxStudents());
                Assertions.assertEquals(classRequestDTO.getSchoolSegment(), savedClass.getSchoolSegment());
                Assertions.assertEquals(classRequestDTO.getTeacherHolder(), savedClass.getTeacherHolder());
                Assertions.assertTrue(classRequestDTO.getStudentsId().containsAll(savedClass.getStudentsId()));

                verify(classService, times(1)).save(any(ClassRequestDTO.class));              
        }

        @Test
        void shouldThrowsRuntimeExceptionWhenStatusIsInvalid() {
                ClassRequestDTO newClassRequest = this.createClassRequestDTO();
                newClassRequest.setTeacherHolder(null);
                UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");
                when(classService.save(newClassRequest)).thenThrow(new BadRequestException("The class cannot be active if it does not have a teacher"));

                Assertions.assertThrows(RuntimeException.class,
                                () -> classController.save(newClassRequest, uriBuilder));

                verify(classService, times(1)).save(any(ClassRequestDTO.class));
        }

        @Test
        void shouldThrowsRuntimeExceptionWhenStudentNotFound() {
                ClassRequestDTO newClassRequest = this.createClassRequestDTO();
                when(classService.save(any(ClassRequestDTO.class)))
                                .thenThrow(new ResourceNotFoundException("Student not found with the given id"));
                UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");

                Assertions.assertThrows(RuntimeException.class,
                                () -> classController.save(newClassRequest, uriBuilder));

                verify(classService, times(1)).save(any(ClassRequestDTO.class));
        }

        @Test
        void shouldThrowsRuntimeExceptionWhenStudentIsAlreadyAssignedToAnotherClass() {
                ClassRequestDTO newClassRequest = this.createClassRequestDTO();
                Student student = this.createStudent();
                student.setClassId(this.createClass());
                UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");
                when(classService.save(any(ClassRequestDTO.class)))
                                .thenThrow(new ConflictException("This student is already allocated to another class"));

                Assertions.assertThrows(RuntimeException.class,
                                () -> classController.save(newClassRequest, uriBuilder));

                verify(classService, times(1)).save(any(ClassRequestDTO.class));
        }


        @Test
        void shouldReturnAClassByIdWhenSuccessful(){
                Class expectedClass = createClass();
                Set<Long> expectedStrudentsId = extractStudentsIdFromStudentSet(expectedClass);
                when(classService.findById(anyLong())).thenReturn(expectedClass);


                ResponseEntity<ClassResponseDTO> responseEntityReturnedClass = classController.searchById(1L);
                

                Assertions.assertNotNull(responseEntityReturnedClass);
                assertEquals(HttpStatus.OK, responseEntityReturnedClass.getStatusCode());
                ClassResponseDTO returnedClass = responseEntityReturnedClass.getBody();

                Assertions.assertEquals(expectedClass.getId(), returnedClass.getId());
                Assertions.assertEquals(expectedClass.getTitle(), returnedClass.getTitle());
                Assertions.assertEquals(expectedClass.getClassShift(), returnedClass.getClassShift());
                Assertions.assertEquals(expectedClass.getClassStatus(), returnedClass.getClassStatus());
                Assertions.assertEquals(expectedClass.getMaxStudents(), returnedClass.getMaxStudents());
                Assertions.assertEquals(expectedClass.getSchoolSegment(), returnedClass.getSchoolSegment());
                Assertions.assertEquals(expectedClass.getTeacherHolder(), returnedClass.getTeacherHolder());
                Assertions.assertTrue(expectedStrudentsId.containsAll(returnedClass.getStudentsId()));
                
                verify(classService, times(1)).findById(anyLong());
        }

        @Test
        void shouldThrowsRuntimeExceptionWhenClassNotFound() {
                when(classService.findById(anyLong())).thenThrow(new ResourceNotFoundException("Class not found with the given id"));

                Assertions.assertThrows(RuntimeException.class,
                                () -> classController.searchById(anyLong()));

                verify(classService, times(1)).findById(anyLong());
        }

        @Test
        void shouldNotHaveAnyReturnWheSuccessful(){

                ResponseEntity<Void> responseEntityReturnedClass = classController.delete(1L);
                

                Assertions.assertNotNull(responseEntityReturnedClass);
                Assertions.assertEquals(HttpStatus.NO_CONTENT, responseEntityReturnedClass.getStatusCode());
                Assertions.assertNull(responseEntityReturnedClass.getBody());
                
                verify(classService, times(1)).delete(anyLong());  
        }

        @Test
        void shouldReturnAnUpdatedClassWhenSuccessful() {
                ClassRequestDTO classUpdateRequest = this.createClassRequestDTO();
                Class classToBeUpdated = this.createClass();
                when(classService.update(any(ClassRequestDTO.class), anyLong())).thenReturn(classToBeUpdated);

                ResponseEntity<ClassResponseDTO> responseEntityUpdatedClass = this.classController.update(classUpdateRequest, 2L);

                Assertions.assertNotNull(responseEntityUpdatedClass);
                assertEquals(HttpStatus.OK, responseEntityUpdatedClass.getStatusCode());
                ClassResponseDTO updatedClassResponse = responseEntityUpdatedClass.getBody();

                Assertions.assertEquals(classToBeUpdated.getId(), updatedClassResponse.getId());
                Assertions.assertEquals(classUpdateRequest.getTitle(), updatedClassResponse.getTitle());
                Assertions.assertEquals(classUpdateRequest.getTeacherHolder(), updatedClassResponse.getTeacherHolder());
                Assertions.assertEquals(classUpdateRequest.getMaxStudents(), updatedClassResponse.getMaxStudents());
                Assertions.assertEquals(classUpdateRequest.getClassShift(), updatedClassResponse.getClassShift());
                Assertions.assertEquals(classUpdateRequest.getClassStatus(), updatedClassResponse.getClassStatus());
                Assertions.assertEquals(classUpdateRequest.getSchoolSegment(), updatedClassResponse.getSchoolSegment());
                Assertions.assertTrue(classUpdateRequest.getStudentsId().containsAll(updatedClassResponse.getStudentsId()));

                verify(classService, times(1)).update(any(ClassRequestDTO.class), anyLong());
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

        Class createClassToBeUpdated() {
                Class newClass = new Class();

                newClass.setId(2L);
                newClass.setTitle("Turma para ser atualizada");
                newClass.setClassShift(ClassShiftEnum.AFTERNOONSHIFT);
                newClass.setClassStatus(ClassStatusEnum.PLANNING);
                newClass.setTeacherHolder("professor a ser atualizado");
                newClass.setSchoolSegment(SchoolSegmentEnum.SECONDCHILDISH);
                newClass.setMaxStudents(40);
                newClass.setStudents(Set.of(this.createStudentToBeUpdate()));

                return newClass;
        }

        private Student createStudentToBeUpdate() {
                Student student = new Student();
                student.setId(2L);
                return student;
        }

        private Student createStudent() {
                Student student = new Student();
                student.setId(1L);
                return student;
        }

        private Set<Long> extractStudentsIdFromStudentSet(Class submetedClass){
                return submetedClass.getStudents()
                        .stream()
                        .map(student -> student.getId())
                        .collect(Collectors.toSet());
        }
}
