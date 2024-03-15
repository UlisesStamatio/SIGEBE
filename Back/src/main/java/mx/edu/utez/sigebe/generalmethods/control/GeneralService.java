package mx.edu.utez.sigebe.generalmethods.control;

import mx.edu.utez.sigebe.access.user.model.User;
import mx.edu.utez.sigebe.access.user.model.UserDto;
import mx.edu.utez.sigebe.access.user.model.UserRepository;
import mx.edu.utez.sigebe.utils.entity.Message;
import mx.edu.utez.sigebe.utils.entity.TypesResponse;
import mx.edu.utez.sigebe.utils.validator.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Optional;

@Transactional
@Service
public class GeneralService {
    private final static Logger logger = LoggerFactory.getLogger(GeneralService.class);
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;

    @Autowired
    public GeneralService(UserRepository repository, PasswordEncoder passwordEncoder, PasswordValidator passwordValidator) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidator = passwordValidator;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Object> getProfile(String email) {
        Optional<User> optionalUser = repository.findFirstByEmail(email);
        return optionalUser.<ResponseEntity<Object>>map(user -> new ResponseEntity<>(new Message(user, "Usuario encontrado", TypesResponse.SUCCESS), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(new Message("Usuario no encontrado", TypesResponse.WARNING), HttpStatus.NOT_FOUND));
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> changePassword(UserDto dto) {
        Optional<User> usOpt = repository.findFirstByEmail(dto.getEmail());
        if (!usOpt.isPresent()) {
            return new ResponseEntity<>(new Message("No se encontró el usuario", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }
        User user = usOpt.get();
        if (!user.isStatus()) {
            return new ResponseEntity<>(new Message("Usuario inhabilitado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return new ResponseEntity<>(new Message("La contraseña no coincide con la anterior", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (passwordEncoder.matches(dto.getPasswordNew(), user.getPassword())) {
            return new ResponseEntity<>(new Message("La nueva contraseña no puede ser igual a la anterior", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        boolean valid = passwordValidator.isValid(dto.getPasswordNew());
        if (!valid) {
            return new ResponseEntity<>(new Message("La contraseña no cumple con las características de contraseña segura", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        user.setPassword(passwordEncoder.encode(dto.getPasswordNew()));
        user.setChangePassword(false);
        user = repository.saveAndFlush(user);
        if (user == null) {
            return new ResponseEntity<>(new Message("No se modificó la contraseña", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message(user, "Contraseña modificada", TypesResponse.SUCCESS), HttpStatus.OK);
    }
}
