package mx.edu.utez.sigebe.access.user.control;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mx.edu.utez.sigebe.access.privilege.control.PrivilegeService;
import mx.edu.utez.sigebe.access.role.control.RoleService;
import mx.edu.utez.sigebe.access.user.model.UserDto;
import mx.edu.utez.sigebe.utils.entity.Message;
import mx.edu.utez.sigebe.utils.entity.PaginationDto;
import mx.edu.utez.sigebe.utils.entity.TypesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/user")
@Api(tags = "Usuarios")
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
public class UserController {
    public static final String USER = "USUARIOS";
    private final UserService service;
    private final RoleService serviceRoles;
    private final PrivilegeService servicePrivileges;


    @Autowired
    public UserController(UserService service, RoleService serviceRoles, PrivilegeService servicePrivileges) {
        this.service = service;
        this.serviceRoles = serviceRoles;
        this.servicePrivileges = servicePrivileges;
    }

    @PostMapping("/all")
    @Secured({USER})
    @ApiOperation(
            value = "Obtiene todos los usuarios registradas",
            notes = "{\n" +
                    "    \"value\": \"busqueda\",\n" +
                    "    \"paginationType\": { \"filter\": \"parametro\"," +
                    "    \"sortBy\":\"parametro\"," +
                    "    \"order\":\"asc\"," +
                    "    \"page\":\0\"," +
                    "    \"limit\":\10\"" +
                    " } \n" +
                    "}"
    )
    public ResponseEntity<Object> findAll(@Validated({PaginationDto.StateGet.class}) @RequestBody PaginationDto paginationDto) throws SQLException {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        UserDetails userDetail = (UserDetails) auth.getPrincipal();
        return service.findAll(paginationDto, userDetail.getUsername());
    }

    @GetMapping("/active")
    @Secured(USER)
    @ApiOperation(
            value = "Obtiene todos los usuarios activos"
    )
    public ResponseEntity<Object> findAllActive() {
        return service.findAllEnabled();
    }

    @PostMapping("")
    @Secured(USER)
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
    public ResponseEntity<Object> save(@Validated({UserDto.Register.class}) @RequestBody UserDto dto) throws SQLException {
        return service.save(dto);
    }

    @PutMapping("")
    @Secured(USER)
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
    public ResponseEntity<Object> update(@Validated(UserDto.Modify.class) @RequestBody UserDto dto) throws SQLException {
        return service.update(dto);
    }

    @ApiOperation(
            value = "Cambia el estado del usuario",
            notes = "{ \"id\": 2 }"
    )
    @PutMapping("/change-status")
    @Secured(USER)
    public ResponseEntity<Object> changeStatus(@Validated(UserDto.ChangeStatus.class) @RequestBody UserDto dto) throws SQLException {
        return service.changeStatus(dto);
    }

    @ApiOperation(
            value = "Obtiene un usuario",
            notes = "{ \"id\": 2 }"
    )
    @PutMapping("/one")
    @Secured(USER)
    public ResponseEntity<Object> getOne(@Validated(UserDto.ChangeStatus.class) @RequestBody UserDto dto) {
        return service.getOne(dto);
    }

    @ApiOperation(
            value = "Desbloquea un usuario por límite de intentos de acceso",
            notes = "{ \"id\": 2 }"
    )
    @PutMapping("/restore")
    @Secured(USER)
    public ResponseEntity<Object> restore(@Validated(UserDto.ChangeStatus.class) @RequestBody UserDto dto) {
        boolean result = !service.restore(dto.getId());
        return new ResponseEntity<>(new Message(result ? true : null,
                result ? "Usuario restaurado" : "Ocurrió un error",
                result ? TypesResponse.SUCCESS : TypesResponse.WARNING),
                result ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(
            value = "Restablece la contraseña del usuario",
            notes = "{ \"id\": 2 }"
    )
    @PutMapping("/restore-password")
    @Secured(USER)
    public ResponseEntity<Object> restorePassword(@Validated(UserDto.Restore.class) @RequestBody UserDto dto) {
        return service.restorePassword(dto);
    }

    @ApiOperation(
            value = "Cambia el rol del usuario",
            notes = "{ \n" +
                    "    \"roles\": [{ \"id\": 1}], \n" +
                    " }"
    )
    @PutMapping("/change-role")
    @Secured(USER)
    public ResponseEntity<Object> changeRole(@Validated(UserDto.ChangeRole.class) @RequestBody UserDto dto) throws SQLException {
        return service.changeRole(dto);
    }

    @GetMapping("/roles")
    @Secured({USER})
    @ApiOperation(
            value = "Obtiene todos los roles activos"
    )
    public ResponseEntity<Object> findAllEnabled() {
        return serviceRoles.findAllEnabled();
    }

    @GetMapping("/privileges")
    @Secured({USER})
    @ApiOperation(
            value = "Obtiene todos los privilegios registrados"
    )
    public ResponseEntity<Object> findAllPrivileges() {
        return servicePrivileges.findAll();
    }
}
