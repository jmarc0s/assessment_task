package br.com.jmarcos.assessment_task.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
