package mx.edu.utez.sigebe.security.service;

import mx.edu.utez.sigebe.access.user.control.UserService;
import mx.edu.utez.sigebe.access.user.model.User;
import mx.edu.utez.sigebe.security.dto.JwtDto;
import mx.edu.utez.sigebe.security.dto.LoginDto;
import mx.edu.utez.sigebe.security.jwt.JwtProvider;
import mx.edu.utez.sigebe.utils.entity.Message;
import mx.edu.utez.sigebe.utils.entity.TypesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

@Service
@Transactional
public class AccessService {

    private final static Logger logger = LoggerFactory.getLogger(AccessService.class);

    @Autowired
    UserService userService;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    AuthenticationManager authenticationManager;

    public ResponseEntity<JwtDto> login(LoginDto loginDto) throws SQLException {
        logger.info("Entra al login");
        Optional<User> optionalUser = userService.findFirstByEmail(loginDto.getEmail());
        if (!optionalUser.isPresent()) {
            return new ResponseEntity(new Message("Usuario no encontrado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        User user = optionalUser.get();
        if (!user.isStatus()) {
            return new ResponseEntity(new Message("Usuario inactivo", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (user.getTimeBlocked() != null) {
            Calendar calendarNow = Calendar.getInstance(TimeZone.getTimeZone("America/Mexico_City"));
            calendarNow.setTime(new Date());
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Mexico_City"));
            calendar.setTime(user.getTimeBlocked());
            calendar.add(Calendar.MINUTE, 30);
            if (calendar.before(calendarNow)) {
                userService.restore(user.getId());
            }
        }
        if (user.getAttempts() >= 4) {
            return new ResponseEntity(new Message("El usuario ha sido bloqueado, vuelva a intentar en 30 minutos", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String tkn = jwtProvider.generateToken(authentication);

            JwtDto jwtDto = new JwtDto(tkn, userDetails.getUsername(), user.getRoles());
            return new ResponseEntity<>(jwtDto, HttpStatus.OK);
        } catch (Exception e) {
            userService.setAttempts(user.getId());
            if (user.getAttempts() >= 4) {
                logger.error("Usuario bloqueado");
                return new ResponseEntity(new Message("El usuario ha sido bloqueado, vuelva a intentar en 30 minutos", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }
            logger.error("Error de autenticación");
            return new ResponseEntity(new Message("Error de credenciales", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
    }
}
