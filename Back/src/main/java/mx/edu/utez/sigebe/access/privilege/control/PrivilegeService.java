package mx.edu.utez.sigebe.access.privilege.control;

import mx.edu.utez.sigebe.access.privilege.model.Privilege;
import mx.edu.utez.sigebe.access.privilege.model.PrivilegeName;
import mx.edu.utez.sigebe.access.privilege.model.PrivilegeRepository;
import mx.edu.utez.sigebe.utils.entity.Message;
import mx.edu.utez.sigebe.utils.entity.TypesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Optional;

@Service
@Transactional
public class PrivilegeService {
    private final static Logger logger = LoggerFactory.getLogger(PrivilegeService.class);
    private final PrivilegeRepository repository;
    @Autowired
    public PrivilegeService(PrivilegeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Object> findAll() {
        return new ResponseEntity<>(new Message(repository.findAll(), "Listado de roles", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public Optional<Privilege> findByName(PrivilegeName name) {
        return repository.findFirstByName(name);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public Privilege saveInitial(Privilege privilege) {
        return repository.saveAndFlush(privilege);
    }

    @Transactional(readOnly = true)
    public Optional<Privilege> findById(Long id) {
        return repository.findById(id);
    }
}
