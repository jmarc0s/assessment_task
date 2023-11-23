package br.com.jmarcos.assessment_task.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "responsible")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Resposible {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; 

    @Column(name = "name")
    private String name;


    @Column(name = "email")
    private String email;


    @Column(name = "phone")
    private String phone;

}
