package mx.edu.utez.sigebe.security.control;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mx.edu.utez.sigebe.access.user.control.UserService;
import mx.edu.utez.sigebe.access.user.model.UserDto;
import mx.edu.utez.sigebe.access.visualconfig.control.VisualConfigService;
import mx.edu.utez.sigebe.security.dto.JwtDto;
import mx.edu.utez.sigebe.security.dto.LoginDto;
import mx.edu.utez.sigebe.security.service.AccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Api(tags = "Acceso")
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET})
public class AccessController {

    private final static Logger logger = LoggerFactory.getLogger(AccessController.class);

    private final AccessService accessService;
    private final UserService userService;
    private final VisualConfigService visualConfigService;


    @Autowired
    public AccessController(AccessService accessService, UserService userService, VisualConfigService visualConfigService) {
        this.accessService = accessService;
        this.userService = userService;
        this.visualConfigService = visualConfigService;
    }

    @ApiOperation(
            value = "Inicio de sesión",
            notes = "{\n" +
                    "    \"email\": \"admin@localhost.com\",\n" +
                    "    \"password\": \"Admin123\"\n" +
                    "}"
    )
    @PostMapping("/login")
    public ResponseEntity<JwtDto> login(@RequestBody LoginDto loginDto) throws Exception {
        return accessService.login(loginDto);
    }

    @ApiOperation(
            value = "Envía correo para recuperación de contraseña",
            notes = "{\n" +
                    "    \"email\": \"admin@localhost.com\",\n" +
                    "}"
    )
    @PostMapping("/recover")
    public ResponseEntity<Object> recover(@Validated({UserDto.Recover.class}) @RequestBody UserDto dto){
        return userService.recover(dto);
    }

    @ApiOperation(
            value = "Valida que el código ingresado sea correcto",
            notes = "{\n" +
                    "    \"email\": \"admin@localhost.com\",\n" +
                    "    \"recuperation\": \"winjM\"\n" +
                    "}"
    )
    @PostMapping("/verify-code")
    public ResponseEntity<Object> verifyCode(@Validated({UserDto.VerifyCode.class}) @RequestBody UserDto dto){
        return userService.verifyCode(dto);
    }

    @ApiOperation(
            value = "Envía la contraseña junto con el correo del usuario y s código",
            notes = "{\n" +
                    "    \"email\": \"noeliabahena@utez.edu.mx\",\n" +
                    "    \"recuperation\": \"winjM\",\n" +
                    "    \"password\": \"Noelia124#\"\n" +
                    "}"
    )
    @PostMapping("/change-password")
    public ResponseEntity<Object> changePasword(@Validated({UserDto.ChangePassword.class}) @RequestBody UserDto dto){
        return userService.updatePassword(dto);
    }

    @GetMapping("/getVisual")
    @ApiOperation(
            value = "Obtiene las configuraciones visuales registradas"
    )
    public ResponseEntity<Object> findAll() {
        return visualConfigService.findAll();
    }
}
