package mx.edu.utez.sigebe.basecatalog.students.control;

import mx.edu.utez.sigebe.basecatalog.person.control.PersonService;
import mx.edu.utez.sigebe.basecatalog.person.model.Person;
import mx.edu.utez.sigebe.basecatalog.person.model.PersonDto;
import mx.edu.utez.sigebe.basecatalog.students.model.Student;
import mx.edu.utez.sigebe.basecatalog.students.model.StudentDto;
import mx.edu.utez.sigebe.basecatalog.students.model.StudentRepository;
import mx.edu.utez.sigebe.utils.entity.Message;
import mx.edu.utez.sigebe.utils.entity.TypesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

@Transactional
@Service
public class StudentService {
    private final static Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository repository;
    private final PersonService personService;

    @Autowired
    public StudentService(StudentRepository repository, PersonService personService) {
        this.repository = repository;
        this.personService = personService;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Object> findAll() {
        return new ResponseEntity<>(new Message(repository.findAll(), "Listado de estudiantes", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Object> findAllEnabled() {
        return new ResponseEntity<>(new Message(repository.findAllByStatusIsTrue(), "Listado de estudiantes activos", TypesResponse.SUCCESS), HttpStatus.OK);
    }


    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> save(StudentDto dto) throws SQLException {
        if (dto.getPerson().getName() == null || dto.getPerson().getSurname() == null
                || Objects.equals(dto.getPerson().getName(), "")
                || Objects.equals(dto.getPerson().getSurname(), "")
                || dto.getPerson().getRfc() == null || dto.getPerson().getCurp() == null
                || dto.getPerson().getSex() == null || dto.getPerson().getBirthDate() == null
                || dto.getPerson().getCellphone() == null || dto.getPerson().getPhone() == null
        ) {
            throw new SQLException("Campos vacíos", String.valueOf(TypesResponse.ERROR));
        }

        if ( dto.getPerson().getName().contains("/") || dto.getPerson().getSurname().contains("/")){
            return new ResponseEntity<>(new Message("Se ingresaron caracteres inválidos", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }


        Optional<Student> optionalUser = repository.searchByEnrollmentAndId(dto.getEnrollment(),0L);
        if (optionalUser.isPresent()) {
            return new ResponseEntity<>(new Message("La matrícula del estudiante ya existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }

        PersonDto personDto = new PersonDto(dto.getPerson());
        ResponseEntity responseEntity = personService.save(personDto);
        Message message = (Message) responseEntity.getBody();
        assert message != null;
        if (!message.getType().equals(TypesResponse.SUCCESS)) {
            return new ResponseEntity<>(new Message(message.getText(), message.getType()), HttpStatus.BAD_REQUEST);
        }

        Student student = new Student();
        student.asignValues(dto);
        student.setStatus(true);
        student.setPerson((Person) message.getResult());

        student = repository.saveAndFlush(student);
        if (student == null) {
            return new ResponseEntity<>(new Message("Usuario no registrado", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message(student, "Usuario registrado", TypesResponse.SUCCESS), HttpStatus.OK);
    }


    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> update(StudentDto dto) throws SQLException {
        Optional<Student> optional = repository.findById(dto.getId());
        if (!optional.isPresent()) {
            return new ResponseEntity<>(new Message("Estudiante no encontrado", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }
        Student student = optional.get();

        dto.getPerson().setId(student.getPerson().getId());
        PersonDto personDto = new PersonDto(dto.getPerson());
        ResponseEntity responseEntity = personService.update(personDto);
        Message message = (Message) responseEntity.getBody();
        assert message != null;
        if (!message.getType().equals(TypesResponse.SUCCESS)) {
            return new ResponseEntity<>(new Message(message.getText(), message.getType()), HttpStatus.BAD_REQUEST);
        }


        Optional<Student> optionalUser = repository.searchByEnrollmentAndId(dto.getEnrollment(),student.getId());
        if (optionalUser.isPresent()) {
            return new ResponseEntity<>(new Message("La matrícula del estudiante ya existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }


        student.asignValues(dto);

        student = repository.saveAndFlush(student);
        if (student == null) {
            return new ResponseEntity<>(new Message("Estudiante no modificado", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message(student, "Estudiante modificado", TypesResponse.SUCCESS), HttpStatus.OK);
    }


    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> changeStatus(StudentDto dto) throws SQLException {
        Optional<Student> optional = repository.findById(dto.getId());
        if (!optional.isPresent()) {
            return new ResponseEntity<>(new Message("Estudiante no encontrado", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }
        Student student = optional.get();
        boolean status = student.isStatus();
        student.setStatus(!status);
        student = repository.saveAndFlush(student);
        if (student == null) {
            return new ResponseEntity<>(new Message("Estado del estudiante no modificado", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message(student, "Estado del estudiante modificado", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Object> getOne(StudentDto dto) {
        Optional<Student> optional = repository.findById(dto.getId());
        return optional.<ResponseEntity<Object>>map(student -> new ResponseEntity<>(new Message(student, "Estudiante encontrado", TypesResponse.SUCCESS), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(new Message("Estudiante no encontrado", TypesResponse.WARNING), HttpStatus.NOT_FOUND));
    }

}
