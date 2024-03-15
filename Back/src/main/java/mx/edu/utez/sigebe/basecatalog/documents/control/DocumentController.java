package mx.edu.utez.sigebe.basecatalog.documents.control;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mx.edu.utez.sigebe.basecatalog.documents.model.DocumentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/documents")
@Api(tags = "Documentos")
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
public class DocumentController {
    public static final String STATES = "ESTADOS";

    private final  DocumentService service;

    @Autowired
    public DocumentController(DocumentService service) {
        this.service = service;
    }

    @GetMapping("")
    @Secured({STATES})
    @ApiOperation(
            value = "Retorna un listado con todos los documentos"
    )
    public ResponseEntity<Object> findAll() {
        return service.findAll();
    }

    @GetMapping("/enabled")
    @Secured({STATES})
    @ApiOperation(
            value = "Retorna un listado con todos los documentos"
    )
    public ResponseEntity<Object> findAllEnabled() {
        return service.findAllEnabled();
    }


    @PostMapping("")
    @Secured({STATES})
    @ApiOperation(
            value = "Registra un documento",
            notes = ""
    )
    public ResponseEntity<Object> save(@Validated(DocumentDto.Register.class) @RequestBody DocumentDto dto) {
        return service.save(dto);
    }

    @PutMapping("")
    @Secured({STATES})
    @ApiOperation(
            value = "Actualiza el documento",
            notes = ""
    )
    public ResponseEntity<Object> update(@Validated({DocumentDto.Modify.class}) @RequestBody DocumentDto dto) {
        return service.update(dto);
    }

    @PutMapping("/change-status")
    @Secured({STATES})
    @ApiOperation(
            value = "Actualiza el estado del documento",
            notes = "{ \"id\": 2 }"
    )
    public ResponseEntity<Object> changeStatus(@Validated(DocumentDto.ChangeStatus.class) @RequestBody DocumentDto dto) {
        return service.changeStatus(dto);
    }
}
