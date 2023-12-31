package br.com.jmarcos.assessment_task.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.jmarcos.assessment_task.controller.DTO.student.StudentRequestDTO;
import br.com.jmarcos.assessment_task.controller.DTO.student.address.AddressRequestDTO;
import br.com.jmarcos.assessment_task.controller.DTO.student.responsible.ResponsibleRequestDTO;
import br.com.jmarcos.assessment_task.model.Address;
import br.com.jmarcos.assessment_task.model.Class;
import br.com.jmarcos.assessment_task.model.Responsible;
import br.com.jmarcos.assessment_task.model.Student;
import br.com.jmarcos.assessment_task.model.User;
import br.com.jmarcos.assessment_task.model.enums.UserTypeEnum;
import br.com.jmarcos.assessment_task.repository.StudentRepository;
import br.com.jmarcos.assessment_task.service.exceptions.BadRequestException;
import br.com.jmarcos.assessment_task.service.exceptions.ConflictException;
import br.com.jmarcos.assessment_task.service.exceptions.ResourceNotFoundException;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Page<Student> search(Pageable pageable) {
        return this.studentRepository.findAll(pageable);
    }

    public Student findById(Long studentId) {
        return this.studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with the given id"));
    }

    public Student save(StudentRequestDTO studentRequest) {
        Student student = this.toStudent(studentRequest);

        return this.studentRepository.save(student);
    }

    // ALINHAR COM REQUISITOS: TALVEZ SÓ É POSSIVEL APAGAR UM ALUNO SE ELE ESTIVER
    // EM UMA TURMA
    public void delete(Long id) {
        Student returnedStudent = this.findById(id);

        this.studentRepository.delete(returnedStudent);
    }

    public Student update(StudentRequestDTO studentRequestDTO,
            Long id) {
        Student oldStudent = this.findById(id);

        Student updatedStudent = fillUpdate(oldStudent, studentRequestDTO);

        this.updateUser(updatedStudent, studentRequestDTO);

        return this.studentRepository.save(updatedStudent);
    }

    private Student toStudent(StudentRequestDTO studentRequest) {
        Student student = new Student();

        student.setName(studentRequest.getName());
        student.setCpf(this.validateCpf(studentRequest.getCpf()));
        student.setDateOfBirth(this.validateDateOfBirth(studentRequest.getDateOfBirth()));
        student.setResponsibles(this.toResponsibles(studentRequest.getResponsibles()));
        student.setAddress(this.toAddress(studentRequest.getAddress()));

        student.setUser(this.createUser(studentRequest));

        return student;
    }

    private User createUser(StudentRequestDTO studentRequest) {
        User user = new User();

        user.setLogin(studentRequest.getCpf());
        user.setPassword(new BCryptPasswordEncoder().encode(studentRequest.getPassword()));
        user.setUserType(Set.of(UserTypeEnum.ROLE_STUDENT));

        return user;
    }

    private LocalDate validateDateOfBirth(String dateOfBirthString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dateOfBirth = LocalDate.parse(dateOfBirthString, formatter);

        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new BadRequestException("Invalid date of birth submitted");
        }
        return dateOfBirth;
    }

    private String validateCpf(String cpf) {
        if (this.studentRepository.existsByCpf(cpf)) {
            throw new ConflictException("CPF is already in use by someone else");
        }

        return cpf;
    }

    private Address toAddress(AddressRequestDTO addressRequest) {
        Address address = new Address();

        address.setStreet(addressRequest.getStreet());
        address.setNumber(addressRequest.getNumber());
        address.setNeighborhood(addressRequest.getNeighborhood());
        address.setComplement(addressRequest.getComplement());

        return address;
    }

    private Set<Responsible> toResponsibles(Set<ResponsibleRequestDTO> responsiblesRequest) {
        Set<Responsible> responsibles = new HashSet<>();

        for (ResponsibleRequestDTO responsibleRequest : responsiblesRequest) {
            Responsible responsible = new Responsible();
            responsible.setName(responsibleRequest.getName());
            responsible.setEmail(responsibleRequest.getEmail());
            responsible.setPhone(responsibleRequest.getPhone());

            responsibles.add(responsible);
        }

        return responsibles;
    }

    public void updateStudent(Student student) {
        this.studentRepository.save(student);
    }

    public void saveSettingClass(Student student) {
        this.studentRepository.save(student);
    }

    private Student fillUpdate(Student oldStudent, StudentRequestDTO studentRequestDTO) {

        oldStudent.setName(studentRequestDTO.getName());
        oldStudent.setCpf(this.validateCpfToUpdate(studentRequestDTO.getCpf(), oldStudent.getId()));
        oldStudent.setResponsibles(this.toResponsibles(studentRequestDTO.getResponsibles()));
        oldStudent.setAddress(this.toAddressUpdate(studentRequestDTO.getAddress(), oldStudent.getAddress().getId()));
        oldStudent.setDateOfBirth(this.validateDateOfBirth(studentRequestDTO.getDateOfBirth()));

        return oldStudent;
    }

    private Address toAddressUpdate(AddressRequestDTO addressRequest, Long id) {
        Address address = new Address();

        address.setId(id);
        address.setStreet(addressRequest.getStreet());
        address.setNumber(addressRequest.getNumber());
        address.setNeighborhood(addressRequest.getNeighborhood());
        address.setComplement(addressRequest.getComplement());

        return address;
    }

    private void updateUser(Student student, StudentRequestDTO studentRequestDTO) {
        student.getUser().setLogin(studentRequestDTO.getCpf());
        student.getUser().setPassword(studentRequestDTO.getPassword());
    }

    private String validateCpfToUpdate(String cpf, Long id) {
        Optional<Student> student = this.studentRepository.findByCpf(cpf);

        student.ifPresent(existingStudent -> {
            if (!Objects.equals(existingStudent.getId(), id)) {
                throw new ConflictException("This cpf is already in use by someone else");
            }
        });

        return cpf;
    }

    public Class findMyClass(User user) {
        Student student = this.studentRepository.findByUserId(user.getId());

        if (Objects.equals(student.getClassId(), null)) {
            throw new BadRequestException("You are not enrolled in any class");
        }
        return student.getClassId();
    }

}
