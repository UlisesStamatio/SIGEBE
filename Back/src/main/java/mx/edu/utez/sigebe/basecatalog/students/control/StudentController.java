package mx.edu.utez.sigebe.basecatalog.students.control;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mx.edu.utez.sigebe.basecatalog.students.model.StudentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/students")
@Api(tags = "Estudiantes")
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
public class StudentController {

    public static final String STUDENTS = "USUARIOS";

    private final StudentService service;

    @Autowired
    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping("")
    @Secured(STUDENTS)
    @ApiOperation(
            value = "Obtiene todos los usuarios"
    )
    public ResponseEntity<Object> findAll() {
        return service.findAll();
    }

    @GetMapping("/active")
    @Secured(STUDENTS)
    @ApiOperation(
            value = "Obtiene todos los usuarios activos"
    )
    public ResponseEntity<Object> findAllActive() {
        return service.findAllEnabled();
    }

    @PostMapping("")
    @Secured(STUDENTS)
    @ApiOperation(
            value = "Registra un usuario",
            notes = "{\n" +
                    "    \"email\": \"noelia@gmail.com\",\n" +
                    "    \"complement\": \"s\",\n" +
                    "    \"emergency\": \"7773792873\",\n" +
                    "    \"person\": {\n" +
                    "        \"name\": \"Noelia\",\n" +
                    "        \"surname\": \"Noelia\",\n" +
                    "        \"curp\": \"BAAA000626MMSHPLa9\",\n" +
                    "        \"rfc\": \"BAAA0006269w9\",\n" +
                    "        \"phone\": \"7773792873\",\n" +
                    "        \"cellphone\": \"+527773792873\",\n" +
                    "        \"birthDate\": \"2000/02/02\",\n" +
                    "        \"sex\": \"m\",\n" +
                    "        \"state\": {  \"id\": 1  }\n" +
                    "    },\n" +
                    "    \"roles\": [ { \"id\": 2 } ]\n" +
                    "}"
    )
    public ResponseEntity<Object> save(@Validated({StudentDto.Register.class}) @RequestBody StudentDto dto) throws SQLException {
        return service.save(dto);
    }

    @PutMapping("")
    @Secured(STUDENTS)
    @ApiOperation(
            value = "Actualiza el usuario con sus roles correspondientes",
            notes = "{\n" +
                    "    \"id\": 8,\n" +
                    "    \"email\": \"noelia@gmail.com\",\n" +
                    "    \"complement\": \"complement\",\n" +
                    "    \"emergency\": \"7773792873\",\n" +
                    "    \"person\": {\n" +
                    "        \"name\": \"Noelia\",\n" +
                    "        \"surname\": \"Noelia\",\n" +
                    "        \"curp\": \"BAPN000627MMSHPLa2\",\n" +
                    "        \"rfc\": \"BAPN0006279w2\",\n" +
                    "        \"phone\": \"7773792873\",\n" +
                    "        \"cellphone\": \"+527773792873\",\n" +
                    "        \"birthDate\": \"20\",\n" +
                    "        \"sex\": \"m\",\n" +
                    "        \"state\": { \"id\": 1 }\n" +
                    "    },\n" +
                    "    \"roles\": [ { \"id\": \"1\" } ]\n" +
                    "}"
    )
    public ResponseEntity<Object> update(@Validated(StudentDto.Modify.class) @RequestBody StudentDto dto) throws SQLException {
        return service.update(dto);
    }

    @ApiOperation(
            value = "Cambia el estado del usuario",
            notes = "{ \"id\": 2 }"
    )
    @PutMapping("/change-status")
    @Secured(STUDENTS)
    public ResponseEntity<Object> changeStatus(@Validated(StudentDto.ChangeStatus.class) @RequestBody StudentDto dto) throws SQLException {
        return service.changeStatus(dto);
    }

    @ApiOperation(
            value = "Obtiene un usuario",
            notes = "{ \"id\": 2 }"
    )
    @PutMapping("/one")
    @Secured(STUDENTS)
    public ResponseEntity<Object> getOne(@Validated(StudentDto.ChangeStatus.class) @RequestBody StudentDto dto) {
        return service.getOne(dto);
    }
}
