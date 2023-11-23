package br.com.jmarcos.assessment_task.controller.DTO.classes;

import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import br.com.jmarcos.assessment_task.model.Class;
import br.com.jmarcos.assessment_task.model.enums.ClassShiftEnum;
import br.com.jmarcos.assessment_task.model.enums.ClassStatusEnum;
import br.com.jmarcos.assessment_task.model.enums.SchoolSegmentEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassResponseDTO {

    private Long id; 

    private String title;

    private ClassStatusEnum classStatus;

    private Set<Long> studentsId;

    private String teacherHolder;

    private SchoolSegmentEnum schoolSegment;

    private ClassShiftEnum classShift;

    private int maxStudents;


    public ClassResponseDTO(Class savedClass) {
        this.id = savedClass.getId();
        this.title = savedClass.getTitle();
        this.classStatus = savedClass.getClassStatus();
        this.teacherHolder = savedClass.getTeacherHolder();
        this.schoolSegment = savedClass.getSchoolSegment();
        this.classShift = savedClass.getClassShift();
        this.maxStudents = savedClass.getMaxStudents();

        this.studentsId = savedClass.getStudents()
            .stream()
            .map(student -> student.getId()).collect(Collectors.toSet());
    }
}
