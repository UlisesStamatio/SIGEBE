package mx.edu.utez.sigebe.access.role.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mx.edu.utez.sigebe.access.privilege.control.PrivilegeService;
import mx.edu.utez.sigebe.access.role.model.RoleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/role")
@Api(tags = "Roles")
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
public class RoleController {
    public static final String ROLES = "ROLES";
    public static final String USER = "USUARIOS";
    private final RoleService service;
    private final PrivilegeService servicePrivileges;

    @Autowired
    public RoleController(RoleService service, PrivilegeService servicePrivileges) {
        this.service = service;
        this.servicePrivileges = servicePrivileges;
    }

    @GetMapping("")
    @Secured({ROLES})
    @ApiOperation(
            value = "Obtiene todos los roles registrados"
    )
    public ResponseEntity<Object> findAll() {
        return service.findAll();
    }


    @PostMapping("")
    @Secured({ROLES})
    @ApiOperation(
            value = "Registra roles con sus privilegios correspondientes",
            notes = "{\n" +
                    "    \"name\": \"Catalogo base\",\n" +
                    "    \"keyRole\": \"CB\",\n" +
                    "    \"privileges\": [ { \"id\": 3 } ]\n" +
                    "}"
    )
    public ResponseEntity<Object> save(@Validated(RoleDto.Register.class) @RequestBody RoleDto dto) {
        return service.save(dto);
    }

    @PutMapping("")
    @Secured({ROLES})
    @ApiOperation(
            value = "Actualiza roles con sus privilegios correspondientes",
            notes = "{\n" +
                    "    \"id\": 2, \n" +
                    "    \"name\": \"Catalogo base\",\n" +
                    "    \"keyRole\": \"CB\",\n" +
                    "    \"privileges\": [ { \"id\": 3 } ]\n" +
                    "}"
    )
    public ResponseEntity<Object> update(@Validated({RoleDto.Modify.class}) @RequestBody RoleDto dto) throws SQLException, JsonProcessingException {
        return service.update(dto);
    }

    @PutMapping("/change-status")
    @Secured({ROLES})
    @ApiOperation(
            value = "Actualiza el estado del rol",
            notes = "{ \"id\": 2 }"
    )
    public ResponseEntity<Object> changeStatus(@Validated(RoleDto.ChangeStatus.class) @RequestBody RoleDto dto) {
        return service.changeStatus(dto);
    }

    @GetMapping("/privileges")
    @Secured({ROLES})
    @ApiOperation(
            value = "Obtiene todos los privilegios registrados"
    )
    public ResponseEntity<Object> findAllPrivileges() {
        return servicePrivileges.findAll();
    }
}
