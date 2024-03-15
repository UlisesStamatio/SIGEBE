package mx.edu.utez.sigebe.basecatalog.person.control;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mx.edu.utez.sigebe.basecatalog.person.model.PersonDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/person")
@Api(tags = "Persona")
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
public class PersonController {
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final PersonService service;

    @Autowired
    public PersonController(PersonService service) {
        this.service = service;
    }

    @ApiOperation(value = "Consultar persona")
    @GetMapping("")
    @Secured({ROLE_ADMIN})
    public ResponseEntity<Object> findAll() {
        return service.findAll();
    }

    @ApiOperation(value = "Consultar persona activa")
    @GetMapping("/enabled")
    @Secured({ROLE_ADMIN})
    public ResponseEntity<Object> findAllEnabled() {
        return service.findAllEnabled();
    }

    @ApiOperation(value = "Registrar persona",
            notes = "{\n" +
                    "    \"name\": \"Ulises\",\n" +
                    "    \"surname\":\"Stamatio\",\n" +
                    "    \"secondSurname\":\"Ferraez\",\n" +
                    "    \"curp\":\"SAFH020611HMSTRCA0\",\n" +
                    "    \"rfc\":\"EARN020919JTA\",\n" +
                    "    \"phone\":\"7776665544\",\n" +
                    "    \"cellphone\":\"7776665544\",\n" +
                    "    \"sex\":\"H\",\n" +
                    "    \"birthDate\":\"1990/05/12\",\n" +
                    "    \"nss\":\"00000000000\",\n" +
                    "    \"address\":{\n" +
                    "        \"settlement\":\"Parres\",\n" +
                    "        \"town\":\"Jiutepec\",\n" +
                    "        \"street\":\"Hacienda de cocoyoc\",\n" +
                    "        \"cp\":62564,\n" +
                    "        \"internalNumber\":\"13\",\n" +
                    "        \"externalNumber\":\"13\"\n" +
                    "    }\n" +
                    "}")
    @PostMapping("")
    @Secured({ROLE_ADMIN})
    public ResponseEntity<Object> save(@Validated(PersonDto.Register.class) @RequestBody PersonDto dto) throws SQLException {
        return service.save(dto);
    }

    @ApiOperation(value = "Modificar persona",
            notes = " { 'id' : 'Long" +
                    "    'name' : 'String' \n" +
                    "    'surname' : 'String' \n" +
                    "    'secondSurname' : 'String' \n")
    @PutMapping("")
    @Secured({ROLE_ADMIN})
    public ResponseEntity<Object> update(@Validated(PersonDto.Modify.class) @RequestBody PersonDto dto) throws SQLException {
        return service.update(dto);
    }

    @ApiOperation(value = "Cambiar estado de la persona",
            notes = "Sólo necesita el id de la persona ")
    @PutMapping("/change-status")
    @Secured({ROLE_ADMIN})
    public ResponseEntity<Object> changeStatus(@Validated(PersonDto.ChangeStatus.class) @RequestBody PersonDto dto) throws SQLException {
        return service.changeStatus(dto);
    }
}