package br.com.jmarcos.assessment_task.controller.DTO.classes;

import java.util.Set;

import br.com.jmarcos.assessment_task.model.enums.ClassStatusEnum;
import br.com.jmarcos.assessment_task.model.enums.SchoolSegmentEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassResponseDTO {

    private Long id; 

    private String title;

    private ClassStatusEnum ClassStatus;

    private Set<Long> studentsId;

    private String teacherHolder;

    private Set<String> teacherAssistents;

    private SchoolSegmentEnum schoolSegment;

    private int maxStudents;
}
