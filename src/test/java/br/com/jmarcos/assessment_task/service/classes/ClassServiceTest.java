package br.com.jmarcos.assessment_task.service.classes;

import br.com.jmarcos.assessment_task.controller.DTO.classes.ClassRequestDTO;
import br.com.jmarcos.assessment_task.model.Class;
import br.com.jmarcos.assessment_task.model.Student;
import br.com.jmarcos.assessment_task.model.enums.ClassShiftEnum;
import br.com.jmarcos.assessment_task.model.enums.ClassStatusEnum;
import br.com.jmarcos.assessment_task.model.enums.SchoolSegmentEnum;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import br.com.jmarcos.assessment_task.repository.ClassRepository;
import br.com.jmarcos.assessment_task.service.ClassService;
import br.com.jmarcos.assessment_task.service.StudentService;
import br.com.jmarcos.assessment_task.service.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class ClassServiceTest {

        @InjectMocks
        private ClassService classService;

        @Mock
        ClassRepository classRepository;

        @Mock
        private StudentService studentService;

        private PageRequest pageable = PageRequest.of(0, 5);

        private List<Class> classList;

        @Test
        void shouldReturnAPageOfClassesWhenSuccessful() {
                when(classRepository.findAll(pageable)).thenReturn(createClassPageImpl(List.of(createClass())));

                List<Class> returnedClassList = classService.search(pageable).toList();

                Assertions.assertFalse(returnedClassList.isEmpty());
                Assertions.assertEquals(classList.get(0).getId(), returnedClassList.get(0).getId());
                Assertions.assertEquals(classList.get(0).getTitle(), returnedClassList.get(0).getTitle());
                Assertions.assertEquals(classList.get(0).getMaxStudents(), returnedClassList.get(0).getMaxStudents());
                Assertions.assertEquals(classList.get(0).getTeacherHolder(),
                                returnedClassList.get(0).getTeacherHolder());
                Assertions.assertEquals(classList.get(0).getClassShift(), returnedClassList.get(0).getClassShift());
                Assertions.assertEquals(classList.get(0).getClassStatus(), returnedClassList.get(0).getClassStatus());
                Assertions.assertEquals(classList.get(0).getSchoolSegment(),
                                returnedClassList.get(0).getSchoolSegment());
                Assertions.assertNotNull(returnedClassList.get(0).getStudents());
                Assertions.assertTrue(
                                returnedClassList.get(0).getStudents().containsAll(classList.get(0).getStudents()));

                verify(classRepository, times(1)).findAll(pageable);
        }

        @Test
        void shouldReturnAnEmptyPageOfClassesWhenThereAreNoClasses() {
                when(classRepository.findAll(pageable)).thenReturn(createClassPageImpl(List.of()));

                List<Class> returnedStudentList = classService.search(pageable).toList();

                Assertions.assertTrue(returnedStudentList.isEmpty());
                assertIterableEquals(classList, returnedStudentList);

                verify(classRepository, times(1)).findAll(pageable);
        }

        @Test
        void shouldReturnAClassByIdWhenSuccessful() {
                Class expectedClass = this.createClass();

                when(classRepository.findById(anyLong()))
                                .thenReturn(Optional.of(expectedClass));

                Class returnedClass = this.classService
                                .findById(1L);

                Assertions.assertNotNull(expectedClass.getTitle(), returnedClass.getTitle());
                Assertions.assertEquals(expectedClass.getTitle(), returnedClass.getTitle());
                Assertions.assertEquals(expectedClass.getTeacherHolder(), returnedClass.getTeacherHolder());
                Assertions.assertEquals(expectedClass.getMaxStudents(), returnedClass.getMaxStudents());
                Assertions.assertEquals(expectedClass.getClassShift(), returnedClass.getClassShift());
                Assertions.assertEquals(expectedClass.getClassStatus(), returnedClass.getClassStatus());
                Assertions.assertEquals(expectedClass.getSchoolSegment(), returnedClass.getSchoolSegment());
                Assertions.assertNotNull(returnedClass.getStudents());
                Assertions.assertTrue(expectedClass.getStudents().containsAll(returnedClass.getStudents()));

                verify(classRepository, times(1)).findById(anyLong());
        }

        @Test
        void shouldThrowsRuntimeExceptionWhenClassNotFound() {

                Assertions.assertThrows(RuntimeException.class,
                                () -> classService.findById(anyLong()));

                verify(classRepository, times(1)).findById(anyLong());
        }

        @Test
        void shouldReturnASavedClassWhenSuccessful() {
                ClassRequestDTO newClassRequest = this.createClassRequestDTO();
                when(classRepository.save(any(Class.class))).thenReturn(this.createClass());
                when(studentService.findById(anyLong())).thenReturn(this.createStudent());

                Class savedClass = this.classService.save(newClassRequest);

                Assertions.assertNotNull(savedClass.getId());
                Assertions.assertEquals(newClassRequest.getTitle(), savedClass.getTitle());
                Assertions.assertEquals(newClassRequest.getTeacherHolder(), savedClass.getTeacherHolder());
                Assertions.assertEquals(newClassRequest.getMaxStudents(), savedClass.getMaxStudents());
                Assertions.assertEquals(newClassRequest.getClassShift(), savedClass.getClassShift());
                Assertions.assertEquals(newClassRequest.getClassStatus(), savedClass.getClassStatus());
                Assertions.assertEquals(newClassRequest.getSchoolSegment(), savedClass.getSchoolSegment());
                Assertions.assertNotNull(savedClass.getStudents());
                Assertions.assertEquals(newClassRequest.getStudentsId()
                                .stream()
                                .findFirst()
                                .get(),
                                savedClass.getStudents()
                                                .stream()
                                                .findFirst().get().getId());

                verify(classRepository, times(1)).save(any(Class.class));
                verify(classRepository, times(1)).findAllByTeacherHolder(anyString());
                verify(studentService, times(1)).findById(anyLong());
                verify(studentService, times(1)).saveSettingClass(any(Student.class));
        }

        @Test
        void shouldThrowsRuntimeExceptionWhenStatusIsInvalid() {
                ClassRequestDTO newClassRequest = this.createClassRequestDTO();
                newClassRequest.setTeacherHolder(null);

                Assertions.assertThrows(RuntimeException.class,
                                () -> classService.save(newClassRequest));

                verify(classRepository, times(0)).save(any(Class.class));
                verify(classRepository, times(0)).findAllByTeacherHolder(anyString());
                verify(studentService, times(0)).findById(anyLong());
                verify(studentService, times(0)).saveSettingClass(any(Student.class));
        }

        @Test
        void shouldThrowsRuntimeExceptionWhenTeacherIsUnavailable() {
                ClassRequestDTO newClassRequest = this.createClassRequestDTO();
                when(classRepository.findAllByTeacherHolder(anyString())).thenReturn(List.of(this.createClass()));

                Assertions.assertThrows(RuntimeException.class,
                                () -> classService.save(newClassRequest));

                verify(classRepository, times(0)).save(any(Class.class));
                verify(classRepository, times(1)).findAllByTeacherHolder(anyString());
                verify(studentService, times(0)).findById(anyLong());
                verify(studentService, times(0)).saveSettingClass(any(Student.class));
        }

        @Test
        void shouldThrowsRuntimeExceptionWhenStudentNotFound() {
                ClassRequestDTO newClassRequest = this.createClassRequestDTO();
                when(studentService.findById(anyLong()))
                                .thenThrow(new ResourceNotFoundException("Student not found with the given id"));

                Assertions.assertThrows(RuntimeException.class,
                                () -> classService.save(newClassRequest));

                verify(classRepository, times(0)).save(any(Class.class));
                verify(classRepository, times(1)).findAllByTeacherHolder(anyString());
                verify(studentService, times(1)).findById(anyLong());
                verify(studentService, times(0)).saveSettingClass(any(Student.class));
        }

        @Test
        void shouldThrowsRuntimeExceptionWhenStudentIsAlreadyAssignedToAnotherClass() {
                ClassRequestDTO newClassRequest = this.createClassRequestDTO();
                Student student = this.createStudent();
                student.setClassId(this.createClass());
                when(studentService.findById(anyLong())).thenReturn(student);

                Assertions.assertThrows(RuntimeException.class,
                                () -> classService.save(newClassRequest));

                verify(classRepository, times(0)).save(any(Class.class));
                verify(classRepository, times(1)).findAllByTeacherHolder(anyString());
                verify(studentService, times(1)).findById(anyLong());
                verify(studentService, times(0)).saveSettingClass(any(Student.class));
        }

        @Test
        void shouldNotHaveAnyReturnWheSuccessful() {
                Class classToDelete = this.createClass();
                when(classRepository.findById(anyLong())).thenReturn(Optional.of(classToDelete));

                this.classService.delete(1L);

                verify(classRepository, times(1)).findById(anyLong());
                verify(classRepository, times(1)).delete(any(Class.class));
                verify(studentService, times(1)).updateStudent(any(Student.class));
        }

        @Test
        void shouldThrowsRuntimeExceptionWhenClassNotFoundOnDelete() {
                when(classRepository.findById(anyLong())).thenReturn(Optional.empty());

                Assertions.assertThrows(RuntimeException.class,
                                () -> classService.delete(anyLong()));

                verify(classRepository, times(1)).findById(anyLong());
                verify(classRepository, times(0)).delete(any(Class.class));
                verify(studentService, times(0)).updateStudent(any(Student.class));
        }

        @Test
        void shouldReturnAnUpdatedClassWhenSuccessful() {
                ClassRequestDTO classUpdateRequest = this.createClassRequestDTO();
                Class classToBeUpdated = this.createClassToBeUpdated();
                when(classRepository.findById(anyLong())).thenReturn(Optional.of(classToBeUpdated));
                when(classRepository.save(any(Class.class))).thenReturn(classToBeUpdated);
                when(studentService.findById(anyLong())).thenReturn(this.createStudent());

                Class updatedClass = this.classService.update(classUpdateRequest, 2L);

                Assertions.assertEquals(classToBeUpdated.getId(), updatedClass.getId());
                Assertions.assertEquals(classUpdateRequest.getTitle(), updatedClass.getTitle());
                Assertions.assertEquals(classUpdateRequest.getTeacherHolder(), updatedClass.getTeacherHolder());
                Assertions.assertEquals(classUpdateRequest.getMaxStudents(), updatedClass.getMaxStudents());
                Assertions.assertEquals(classUpdateRequest.getClassShift(), updatedClass.getClassShift());
                Assertions.assertEquals(classUpdateRequest.getClassStatus(), updatedClass.getClassStatus());
                Assertions.assertEquals(classUpdateRequest.getSchoolSegment(), updatedClass.getSchoolSegment());
                Assertions.assertNotNull(updatedClass.getStudents());
                Assertions.assertEquals(classUpdateRequest.getStudentsId()
                                .stream()
                                .findFirst()
                                .get(),
                                updatedClass.getStudents()
                                                .stream()
                                                .findFirst().get().getId());

                verify(classRepository, times(1)).save(any(Class.class));
                verify(classRepository, times(1)).findById(anyLong());
                verify(classRepository, times(1)).findAllByTeacherHolder(anyString());
                verify(studentService).findById(anyLong());
                verify(studentService, times(2)).saveSettingClass(any(Student.class));
        }

        @Test
        void shouldThrowsRuntimeExceptionWhenStatusIsInvalidOnUpdate() {
                ClassRequestDTO classUpdateRequest = this.createClassRequestDTO();
                Class classToBeUpdated = this.createClassToBeUpdated();
                when(classRepository.findById(anyLong())).thenReturn(Optional.of(classToBeUpdated));
                classUpdateRequest.setTeacherHolder(null);

                Assertions.assertThrows(RuntimeException.class,
                                () -> classService.update(classUpdateRequest, 2L));

                verify(classRepository, times(0)).save(any(Class.class));
                verify(classRepository, times(1)).findById(anyLong());
                verify(classRepository, times(0)).findAllByTeacherHolder(anyString());
                verify(studentService, times(0)).findById(anyLong());
                verify(studentService, times(0)).saveSettingClass(any(Student.class));
        }

        @Test
        void shouldThrowsRuntimeExceptionWhenTeacherIsUnavailableOnUpdate() {
                ClassRequestDTO classUpdateRequest = this.createClassRequestDTO();
                Class classToBeUpdated = this.createClassToBeUpdated();
                when(classRepository.findById(anyLong())).thenReturn(Optional.of(classToBeUpdated));
                when(classRepository.findAllByTeacherHolder(anyString())).thenReturn(Arrays.asList(this.createClass()));

                Assertions.assertThrows(RuntimeException.class,
                                () -> classService.update(classUpdateRequest, 2L));

                verify(classRepository, times(0)).save(any(Class.class));
                verify(classRepository, times(1)).findById(anyLong());
                verify(classRepository, times(1)).findAllByTeacherHolder(anyString());
                verify(studentService, times(0)).findById(anyLong());
                verify(studentService, times(0)).saveSettingClass(any(Student.class));
        }

        @Test
        void shouldThrowsRuntimeExceptionWhenClassNotFoundOnUpdate() {
                ClassRequestDTO classUpdateRequest = this.createClassRequestDTO();

                Assertions.assertThrows(RuntimeException.class,
                                () -> classService.update(classUpdateRequest, 2L));

                verify(classRepository, times(0)).save(any(Class.class));
                verify(classRepository, times(0)).findAllByTeacherHolder(anyString());
                verify(classRepository, times(1)).findById(anyLong());
                verify(studentService, times(0)).findById(anyLong());
                verify(studentService, times(0)).saveSettingClass(any(Student.class));
        }

        @Test
        void shouldThrowsRuntimeExceptionWhenStudentIsAlreadyAssignedToAnotherClassOnUpdate() {
                ClassRequestDTO classUpdateRequest = this.createClassRequestDTO();
                Class classToBeUpdated = this.createClassToBeUpdated();
                Student student = this.createStudent();
                student.setClassId(this.createClass());
                when(classRepository.findById(anyLong())).thenReturn(Optional.of(classToBeUpdated));
                when(studentService.findById(anyLong())).thenReturn(student);

                Assertions.assertThrows(RuntimeException.class,
                                () -> classService.update(classUpdateRequest, 2L));

                verify(classRepository, times(1)).findById(anyLong());
                verify(classRepository, times(0)).save(any(Class.class));
                verify(classRepository, times(1)).findAllByTeacherHolder(anyString());
                verify(studentService, times(1)).findById(anyLong());
                verify(studentService, times(0)).saveSettingClass(any(Student.class));
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

        private PageImpl<Class> createClassPageImpl(List<Class> list) {
                classList = list;
                PageImpl<Class> classPage = new PageImpl<>(classList);

                return classPage;
        }
}
