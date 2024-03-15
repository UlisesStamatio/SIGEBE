package mx.edu.utez.sigebe.generalmethods.control;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mx.edu.utez.sigebe.access.user.model.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/general")
@Api(tags = "General")
@CrossOrigin(origins = {"*"}, methods = { RequestMethod.GET,RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class GeneralController {

    private final static Logger logger = LoggerFactory.getLogger(GeneralController.class);

    private final GeneralService service;

    @Autowired
    public GeneralController(GeneralService service) {
        this.service = service;
    }


    @ApiOperation(
            value = "Obtiene el perfil en sesión"
    )
    @GetMapping("/profile")
    public ResponseEntity<Object> getProfile() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        UserDetails userDetail = (UserDetails) auth.getPrincipal();
        return service.getProfile(userDetail.getUsername());
    }

    @ApiOperation(
            value = "Cambio de contraseña del usuario",
            notes = "\"email\": \"String\",\n" +
                    "    \"password\": \"String\",\n" +
                    "    \"passwordNew\": \"String\""
    )
    @PutMapping("/change-password")
    public ResponseEntity<Object> changePassword(@Validated(UserDto.UpdatePassword.class) @RequestBody UserDto dto) throws SQLException {
        return service.changePassword(dto);
    }



}
