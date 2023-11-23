package br.com.jmarcos.assessment_task.controller.DTO.classes;

import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import br.com.jmarcos.assessment_task.model.enums.ClassStatusEnum;
import br.com.jmarcos.assessment_task.model.enums.SchoolSegmentEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassRequestDTO {


    @NotBlank
    @NotNull
    private String title;

    private String teacherHolder;

    private Set<String> teacherAssistents;

    private Set<Long> studentsId;

    @NotNull
    private ClassStatusEnum ClassStatus;

    @NotNull
    private SchoolSegmentEnum schoolSegment;

    @NotNull
    @Positive
    private int maxStudents;
}
