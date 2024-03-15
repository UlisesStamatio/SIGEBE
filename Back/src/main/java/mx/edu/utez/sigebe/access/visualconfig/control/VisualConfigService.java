package mx.edu.utez.sigebe.access.visualconfig.control;

import mx.edu.utez.sigebe.access.visualconfig.model.VisualConfig;
import mx.edu.utez.sigebe.access.visualconfig.model.VisualConfigDto;
import mx.edu.utez.sigebe.utils.entity.Message;
import mx.edu.utez.sigebe.utils.entity.TypesResponse;
import mx.edu.utez.sigebe.access.visualconfig.model.VisualConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Optional;

@Service
@Transactional
public class VisualConfigService {
    private final static Logger logger = LoggerFactory.getLogger(VisualConfigService.class);
    private final VisualConfigRepository repository;

    @Autowired
    public VisualConfigService(VisualConfigRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public boolean exists() {
        return  repository.findAll().isEmpty();
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Object> findAll() {
        return new ResponseEntity<>(new Message(repository.findAll(Pageable.unpaged()), "Listado de configuraciones visuales", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> save(VisualConfigDto dto) {
        VisualConfig visualConfig = new VisualConfig();
        visualConfig.assign(dto);
        visualConfig = repository.save(visualConfig);
        if (visualConfig == null) {
            return new ResponseEntity<>(new Message("No se registró la Configuración visual", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message(visualConfig, "Se registró la Configuración visual", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> update(VisualConfigDto dto) {
        Optional<VisualConfig> optional = repository.findById(dto.getId());
        if (!optional.isPresent()) {
            return new ResponseEntity<>(new Message("No se encontró la Configuración visual", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }
        VisualConfig visulaConfig = optional.get();
        visulaConfig.assign(dto);
        visulaConfig = repository.save(visulaConfig);
        if (visulaConfig == null) {
            return new ResponseEntity<>(new Message("No se modificó la Configuración visual", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message(visulaConfig, "Se modificó la Configuración visual", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> changeStatus(VisualConfigDto dto) {
        Optional<VisualConfig> optional = repository.findById(dto.getId());
        if (!optional.isPresent()) {
            return new ResponseEntity<>(new Message("No se encontró la Configuración visual", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }
        VisualConfig visulaConfig = optional.get();
        visulaConfig.setStatus(!visulaConfig.isStatus());
        visulaConfig = repository.save(visulaConfig);
        if (visulaConfig == null) {
            return new ResponseEntity<>(new Message("No se modificó el estado de la Configuración visual", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message(visulaConfig, "Se modificó el estado de la Configuración visual", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public VisualConfig saveInitialState(VisualConfig visualConfig) {
        return repository.saveAndFlush(visualConfig);
    }

}
