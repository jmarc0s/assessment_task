package br.com.jmarcos.assessment_task.controller.classes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.jmarcos.assessment_task.controller.ClassController;
import br.com.jmarcos.assessment_task.controller.DTO.classes.ClassRequestDTO;
import br.com.jmarcos.assessment_task.controller.DTO.classes.ClassResponseDTO;
import br.com.jmarcos.assessment_task.model.Student;
import br.com.jmarcos.assessment_task.model.Class;
import br.com.jmarcos.assessment_task.model.enums.ClassShiftEnum;
import br.com.jmarcos.assessment_task.model.enums.ClassStatusEnum;
import br.com.jmarcos.assessment_task.model.enums.SchoolSegmentEnum;
import br.com.jmarcos.assessment_task.service.ClassService;

@ExtendWith(MockitoExtension.class)
public class ClassControllerTest {

        @InjectMocks
        private ClassController classController;

        @Mock
        private ClassService classService;


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

                verify(classService, times(1)).save(any(ClassRequestDTO.class));              
        }

        @Test
        void shouldReturnAClassByIdWhenSuccessful(){
                Class expectedClass = createClass();
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
}
