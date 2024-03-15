package mx.edu.utez.sigebe.generalmethods.control;


import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/lists")
@Api(tags = "Listados Activos")
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class ActiveController {


}
