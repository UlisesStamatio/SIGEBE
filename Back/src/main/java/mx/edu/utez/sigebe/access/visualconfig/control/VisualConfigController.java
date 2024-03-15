package mx.edu.utez.sigebe.access.visualconfig.control;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mx.edu.utez.sigebe.access.visualconfig.model.VisualConfigDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/visual-config")
@Api(tags = "Configuraciones visuales")
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
public class VisualConfigController {

    public static final String VISUAL_CONFIGURATION = "CONFIGURACIONES_VISUALES";
    private final VisualConfigService service;

    @Autowired
    public VisualConfigController(VisualConfigService service) {
        this.service = service;
    }

    @GetMapping("")
    @Secured({VISUAL_CONFIGURATION})
    @ApiOperation(
            value = "Obtiene todas las configuraciones visuales registradas"
    )
    public ResponseEntity<Object> findAll() {
        return service.findAll();
    }

    @PostMapping("")
    @Secured({VISUAL_CONFIGURATION})
    @ApiOperation(
            value = "Registra una nueva configuración visual",
            notes = "{\n" +
                    "    \"backgroundHeader\": \"#FFFFFF\",\n" +
                    "    \"textHeader\": \" #691D0B\",\n" +
                    "    \"backgroundAside\": \"#FFFFFF\",\n" +
                    "    \"textAside\": \" #691D0B\",\n" +
                    "    \"logoLogin\": \"https://ucj.edu.mx/wp-content/uploads/2021/10/ucj-logo.png\",\n" +
                    "    \"logoTop\": \"https://ucj.edu.mx/wp-content/uploads/2021/10/ucj-logo.png\",\n" +
                    "    \"logoEmail\": \"https://ucj.edu.mx/wp-content/uploads/2021/10/ucj-logo.png\",\n" +
                    "}"
    )
    public ResponseEntity<Object> save(@Validated(VisualConfigDto.Register.class) @RequestBody VisualConfigDto dto) {
        return service.save(dto);
    }

    @PutMapping("")
    @Secured({VISUAL_CONFIGURATION})
    @ApiOperation(
            value = "Actualiza una configuración visual",
            notes = "{\n" +
                    "    \"id\": \1\",\n" +
                    "    \"backgroundHeader\": \"#FFFFFF\",\n" +
                    "    \"textHeader\": \" #691D0B\",\n" +
                    "    \"backgroundAside\": \"#FFFFFF\",\n" +
                    "    \"textAside\": \" #691D0B\",\n" +
                    "    \"logoLogin\": \"https://ucj.edu.mx/wp-content/uploads/2021/10/ucj-logo.png\",\n" +
                    "    \"logoTop\": \"https://ucj.edu.mx/wp-content/uploads/2021/10/ucj-logo.png\",\n" +
                    "    \"logoEmail\": \"https://ucj.edu.mx/wp-content/uploads/2021/10/ucj-logo.png\",\n" +
                    "}"
    )
    public ResponseEntity<Object> update(@Validated({VisualConfigDto.Modify.class}) @RequestBody VisualConfigDto dto) {
        return service.update(dto);
    }

    @PutMapping("/change-status")
    @Secured({VISUAL_CONFIGURATION})
    @ApiOperation(
            value = "Cambia el estado de una configuración visual",
            notes = "{\n" +
                    "    \"id\": \1\",\n" +
                    "}"
    )
    public ResponseEntity<Object> changeStatus(@Validated(VisualConfigDto.ChangeStatus.class) @RequestBody VisualConfigDto dto) {
        return service.changeStatus(dto);
    }
}
