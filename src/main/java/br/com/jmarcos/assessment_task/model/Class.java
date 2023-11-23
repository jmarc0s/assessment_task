package br.com.jmarcos.assessment_task.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.jmarcos.assessment_task.model.enums.ClassShiftEnum;
import br.com.jmarcos.assessment_task.model.enums.ClassStatusEnum;
import br.com.jmarcos.assessment_task.model.enums.SchoolSegmentEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "class")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Class {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; 

    @Column(name = "title")
    private String title;

    @Enumerated(EnumType.STRING)
    private ClassStatusEnum ClassStatus;

    @Column(name = "teacher_holder_name")
    private String teacherHolder;

    @ElementCollection
    private Set<String> teacherAssistents;

    @Enumerated(EnumType.STRING)
    private SchoolSegmentEnum schoolSegment;

    @Column(name = "students_id")
    private int maxStudents;

    private ClassShiftEnum classShift;
    

    //private Set<Student> students;
}
