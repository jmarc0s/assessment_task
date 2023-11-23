package br.com.jmarcos.assessment_task.controller.DTO.classes;

import java.util.Set;


import br.com.jmarcos.assessment_task.model.enums.ClassShiftEnum;
import br.com.jmarcos.assessment_task.model.enums.ClassStatusEnum;
import br.com.jmarcos.assessment_task.model.enums.SchoolSegmentEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassRequestDTO {

    @NotBlank
    @NotNull
    private String title;

    private String teacherHolder;

    private Set<Long> studentsId;

    @NotNull
    private ClassStatusEnum classStatus;

    @NotNull
    private SchoolSegmentEnum schoolSegment;

    private ClassShiftEnum classShift;
    
    private int maxStudents;


}
