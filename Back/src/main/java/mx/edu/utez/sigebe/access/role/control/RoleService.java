package mx.edu.utez.sigebe.access.role.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import mx.edu.utez.sigebe.access.privilege.control.PrivilegeService;
import mx.edu.utez.sigebe.access.privilege.model.Privilege;
import mx.edu.utez.sigebe.access.role.model.Role;
import mx.edu.utez.sigebe.access.role.model.RoleDto;
import mx.edu.utez.sigebe.access.role.model.RoleRepository;
import mx.edu.utez.sigebe.access.user.control.UserService;
import mx.edu.utez.sigebe.access.user.model.User;
import mx.edu.utez.sigebe.utils.entity.Message;
import mx.edu.utez.sigebe.utils.entity.TypesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RoleService {
    private final static Logger logger = LoggerFactory.getLogger(RoleService.class);
    private final RoleRepository repository;
    private final PrivilegeService privilegeService;
    @Lazy
    @Autowired
    private UserService userService;
    @Autowired
    public RoleService(RoleRepository repository, PrivilegeService privilegeService) {
        this.repository = repository;
        this.privilegeService = privilegeService;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Object> findAll() {
        return new ResponseEntity<>(new Message(repository.findAll(), "Listado de roles", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Object> findAllEnabled() {
        return new ResponseEntity<>(new Message(repository.findAllByStatus(true), "Listado de roles activos", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    public Optional<Role> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Role> findByKeyRole(String keyRole) {
        return repository.findRoleByKeyRole(keyRole);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public Role saveInitial(Role role) {
        return repository.saveAndFlush(role);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> save(RoleDto dto) {
        dto.setName(dto.getName().toLowerCase());
        dto.setKeyRole(dto.getKeyRole().toLowerCase());
        if(dto.getKeyRole().length()<3 || dto.getKeyRole().length()>10){
            return new ResponseEntity<>(new Message("La clave del Rol debe tener entre 3 y 10 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if(dto.getName().length()<5 || dto.getName().length()>50){
            return new ResponseEntity<>(new Message("El nombre del Rol debe tener entre 5 y 50 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        Optional<Role> optionalRole = repository.searchByNameAndId(dto.getName(), 0L);
        if (optionalRole.isPresent()) {
            return new ResponseEntity<>(new Message("El nombre del Rol ya existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        Optional<Role> roleOptional = repository.searchByKeyRoleAndId(dto.getKeyRole(), 0L);
        if (roleOptional.isPresent()) {
            return new ResponseEntity<>(new Message("La clave del Rol ya existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getPrivileges().isEmpty()) {
            return new ResponseEntity<>(new Message("No ingresó privilegios", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        List<Privilege> privileges = setPrivileges(dto.getPrivileges());
        Role role = new Role();
        role.asignValuesRegister(dto);
        role.setPrivileges(privileges);
        role = repository.saveAndFlush(role);
        if (role == null) {
            return new ResponseEntity<>(new Message("No se registró el Rol", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message(role, "Se registró el Rol", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> update(RoleDto dto) throws SQLException, JsonProcessingException {
        dto.setName(dto.getName().toLowerCase());
        dto.setKeyRole(dto.getKeyRole().toLowerCase());
        if(dto.getKeyRole().length()<3 || dto.getKeyRole().length()>10){
            return new ResponseEntity<>(new Message("La clave del Rol debe tener entre 3 y 10 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if(dto.getName().length()<5 || dto.getName().length()>50){
            return new ResponseEntity<>(new Message("El nombre del Rol debe tener entre 5 y 50 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        Optional<Role> optional = repository.findById(dto.getId());
        if (!optional.isPresent()) {
            return new ResponseEntity<>(new Message("No se encontró el rol", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }
        Optional<Role> optionalRole = repository.searchByNameAndId(dto.getName(), dto.getId());
        if (optionalRole.isPresent()) {
            return new ResponseEntity<>(new Message("El nombre del Rol ya existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        Optional<Role> roleOptional = repository.searchByKeyRoleAndId(dto.getKeyRole(), dto.getId());
        if (roleOptional.isPresent()) {
            return new ResponseEntity<>(new Message("La clave del Rol ya existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        List<Privilege> privileges = setPrivileges(dto.getPrivileges());
        Role role = optional.get();
        role.asignValues(dto);
        role.setPrivileges(privileges);
        role = repository.saveAndFlush(role);
        if (role == null) {
            return new ResponseEntity<>(new Message("No se modificó el Rol", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        List<User> users = userService.searchAllByRoles(optional.get().getKeyRole());
        for (User user : users) {
            userService.updateRole(user);
        }
        return new ResponseEntity<>(new Message(role, "Se modificó el Rol", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> changeStatus(RoleDto dto) {
        Optional<Role> optional = repository.findById(dto.getId());
        if (!optional.isPresent()) {
            return new ResponseEntity<>(new Message("No se encontró el Rol", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }
        List<User> users = userService.searchAllByRoles(optional.get().getKeyRole());
        if (!users.isEmpty()) {
            return new ResponseEntity<>(new Message("El rol está asignado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        Role role = optional.get();
        role.setStatus(!role.isStatus());
        role = repository.saveAndFlush(role);
        if (role == null) {
            return new ResponseEntity<>(new Message("No se modificó el estado del Rol", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message(role, "Se modificó el estado del Rol", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    private List<Privilege> setPrivileges(List<Privilege> list) {
        List<Privilege> privileges = new ArrayList<>();
        for (Privilege privilege : list) {
            Optional<Privilege> optionalPrivilege = privilegeService.findById(privilege.getId());
            if (!optionalPrivilege.isPresent()) {
                continue;
            }
            privileges.add(optionalPrivilege.get());
        }
        return privileges;
    }
}
