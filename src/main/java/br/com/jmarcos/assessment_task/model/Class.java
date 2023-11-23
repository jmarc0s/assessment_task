package br.com.jmarcos.assessment_task.model;

import java.util.Set;




import br.com.jmarcos.assessment_task.model.enums.ClassShiftEnum;
import br.com.jmarcos.assessment_task.model.enums.ClassStatusEnum;
import br.com.jmarcos.assessment_task.model.enums.SchoolSegmentEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

    @Enumerated(EnumType.STRING)
    private SchoolSegmentEnum schoolSegment;

    @Column(name = "students_id")
    private int maxStudents;

    private ClassShiftEnum classShift;
    

    @OneToMany(mappedBy = "classId", fetch = FetchType.LAZY)
    private Set<Student> students;
}
