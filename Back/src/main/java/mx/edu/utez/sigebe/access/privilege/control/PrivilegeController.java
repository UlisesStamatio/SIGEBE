package mx.edu.utez.sigebe.access.privilege.control;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/privilege")
@Api(tags = "Privilegios")
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
public class PrivilegeController {
    public static final String PRIVILEGES = "PRIVILEGIOS";

    private final PrivilegeService service;

    @Autowired
    public PrivilegeController(PrivilegeService service) {
        this.service = service;
    }

    @GetMapping("")
    @Secured({PRIVILEGES})
    @ApiOperation(
            value = "Obtiene todos los privilegios registrados"
    )
    public ResponseEntity<Object> findAll() {
        return service.findAll();
    }
}
