package br.com.jmarcos.assessment_task.controller.classes;

import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.jmarcos.assessment_task.controller.DTO.classes.ClassRequestDTO;
import br.com.jmarcos.assessment_task.model.Student;
import br.com.jmarcos.assessment_task.model.Class;
import br.com.jmarcos.assessment_task.model.enums.ClassShiftEnum;
import br.com.jmarcos.assessment_task.model.enums.ClassStatusEnum;
import br.com.jmarcos.assessment_task.model.enums.SchoolSegmentEnum;
import br.com.jmarcos.assessment_task.service.ClassService;

@WebMvcTest
public class ClassControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ClassService classService;


    @Test
    void shouldReturnASavedClassWhenSuccessful() throws JsonProcessingException, Exception{
        ClassRequestDTO classRequestDTO = createClassRequestDTO();
        Class newClass = createClass();
        when(classService.save(any(ClassRequestDTO.class))).thenReturn(newClass);

        ResultActions response = mockMvc.perform(post("/class")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(classRequestDTO)));

        response.andExpect(status().isCreated())
            .andExpect(jsonPath("$.title", is(classRequestDTO.getTitle())));
            
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
