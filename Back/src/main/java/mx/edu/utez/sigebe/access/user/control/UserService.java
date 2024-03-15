package mx.edu.utez.sigebe.access.user.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import mx.edu.utez.sigebe.access.role.control.RoleService;
import mx.edu.utez.sigebe.access.role.model.Role;
import mx.edu.utez.sigebe.access.user.model.User;
import mx.edu.utez.sigebe.access.user.model.UserDto;
import mx.edu.utez.sigebe.access.user.model.UserRepository;
import mx.edu.utez.sigebe.basecatalog.person.control.PersonService;
import mx.edu.utez.sigebe.basecatalog.person.model.Person;
import mx.edu.utez.sigebe.basecatalog.person.model.PersonDto;
import mx.edu.utez.sigebe.security.dto.JwtDto;
import mx.edu.utez.sigebe.security.jwt.JwtProvider;
import mx.edu.utez.sigebe.utils.entity.Consult;
import mx.edu.utez.sigebe.utils.entity.Message;
import mx.edu.utez.sigebe.utils.entity.PaginationDto;
import mx.edu.utez.sigebe.utils.entity.TypesResponse;
import mx.edu.utez.sigebe.utils.service.EmailService;
import mx.edu.utez.sigebe.utils.validator.EmailValidator;
import mx.edu.utez.sigebe.utils.validator.PasswordValidator;
import mx.edu.utez.sigebe.utils.validator.PhoneValidator;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;

@Transactional
@Service
public class UserService {
    private final static Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;
    private final PhoneValidator phoneValidator;
    private final EmailValidator emailValidator;
    private final EmailService emailService;
    private final RoleService roleService;
    private final PersonService personService;

    @Lazy
    private final JwtProvider jwtProvider;


    @Autowired
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, PasswordValidator passwordValidator, PhoneValidator phoneValidator, EmailValidator emailValidator, EmailService emailService, RoleService roleService, PersonService personService, JwtProvider jwtProvider) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidator = passwordValidator;
        this.phoneValidator = phoneValidator;
        this.emailValidator = emailValidator;
        this.emailService = emailService;
        this.roleService = roleService;
        this.personService = personService;
        this.jwtProvider = jwtProvider;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Object> findAll(PaginationDto paginationDto, String textFilter) throws SQLException {
        if (paginationDto.getPaginationType().getFilter() == null || paginationDto.getPaginationType().getFilter().isEmpty() ||
                paginationDto.getPaginationType().getSortBy() == null || paginationDto.getPaginationType().getSortBy().isEmpty() ||
                paginationDto.getPaginationType().getOrder() == null || paginationDto.getPaginationType().getOrder().isEmpty()
        )
            throw new SQLException("Campos vacíos", String.valueOf(TypesResponse.ERROR));

        if (!paginationDto.getPaginationType().getFilter().equals("name") && !paginationDto.getPaginationType().getFilter().equals("email") || !paginationDto.getPaginationType().getSortBy().equals("name") && !paginationDto.getPaginationType().getSortBy().equals("email"))
            return new ResponseEntity<>(new Message("Organización inválida", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);

        if (!paginationDto.getPaginationType().getOrder().equals("asc") && !paginationDto.getPaginationType().getOrder().equals("desc"))
            return new ResponseEntity<>(new Message("Orden inválido", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);

        paginationDto.setValue("%" + paginationDto.getValue() + "%");
        long count = repository.searchCount();
        List<User> list;
        switch (paginationDto.getPaginationType().getFilter()) {
            case "email":
                list = repository.searchAllByPaginationEmail(
                        paginationDto.getValue(), textFilter,
                        PageRequest.of(paginationDto.getPaginationType().getPage(),
                                paginationDto.getPaginationType().getLimit(),
                                paginationDto.getPaginationType().getOrder().equalsIgnoreCase("ASC")
                                        ? Sort.by(paginationDto.getPaginationType().getSortBy()).ascending()
                                        : Sort.by(paginationDto.getPaginationType().getSortBy()).descending())
                );
                break;
            case "name":
                list = repository.searchAllByPaginationName(
                        paginationDto.getValue(), textFilter,
                        PageRequest.of(paginationDto.getPaginationType().getPage(),
                                paginationDto.getPaginationType().getLimit(),
                                paginationDto.getPaginationType().getOrder().equalsIgnoreCase("ASC")
                                        ? Sort.by(paginationDto.getPaginationType().getSortBy()).ascending()
                                        : Sort.by(paginationDto.getPaginationType().getSortBy()).descending())
                );
                break;
            default:
                return new ResponseEntity<>(new Message("Búsqueda inválida", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message(new Consult(list,
                count),
                "Listado de usuarios registrados",
                TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Object> findAllEnabled() {
        return new ResponseEntity<>(new Message(repository.findAllByStatus(true), "Listado de usuarios activos", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<User> findFirstByEmail(String email) {
        return repository.findFirstByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean existsByMail(String email) {
        Optional<User> optionalUser = repository.findFirstByEmail(email);
        return optionalUser.isPresent();
    }

    @Transactional(rollbackFor = {SQLException.class})
    public void saveInitialUser(User user) {
        repository.save(user);
    }

    public List<User> searchAllByRoles(String role) {
        return repository.searchAllByRole("%" + role + "%");
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> changeRole(UserDto dto) throws SQLException {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        UserDetails userDetail = (UserDetails) auth.getPrincipal();
        if (dto.getRoles() == null) {
            throw new SQLException("Campos vacíos", String.valueOf(TypesResponse.ERROR));
        }
        Optional<User> optionalUser = repository.findFirstByEmail(userDetail.getUsername());
        if (!optionalUser.isPresent()) {
            return new ResponseEntity<>(new Message("Usuario no encontrado", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }
        if (dto.getRoles().isEmpty()) {
            return new ResponseEntity<>(new Message("No ingresó roles", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        Optional<Role> roleOptional = roleService.findById(dto.getRoles().get(0).getId());
        if (!roleOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Rol no encontrado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        User user = optionalUser.get();
        Optional<User> userOptional = repository.searchUserWithRole("%" + roleOptional.get().getKeyRole() + "%", user.getId());
        if (!userOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Rol no encontrado en el usuario", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        String tkn = jwtProvider.generateToken(auth);
        JwtDto jwtDto = new JwtDto(tkn, userDetail.getUsername(), Collections.singletonList(roleOptional.get()));
        return new ResponseEntity<>(jwtDto, HttpStatus.OK);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> save(UserDto dto) throws SQLException {
        if (dto.getEmail() == null
        ) {
            throw new SQLException("Campos vacíos", String.valueOf(TypesResponse.ERROR));
        }


        if (!emailValidator.isValid(dto.getEmail())) {
            return new ResponseEntity<>(new Message("Email malformado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        Optional<User> optionalUser = repository.findFirstByEmail(dto.getEmail());
        if (optionalUser.isPresent()) {
            return new ResponseEntity<>(new Message("El correo electrónico del usuario ya existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getRoles().isEmpty()) {
            return new ResponseEntity<>(new Message("No ingresó roles", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.asignValuesRegister(dto);
        user.setPassword(passwordEncoder.encode(dto.getEmail()));
        String roles = setRoles(dto.getRoles());
        user.setRoles(roles);

        user = repository.saveAndFlush(user);
        if (user == null) {
            return new ResponseEntity<>(new Message("Usuario no registrado", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message(user, "Usuario registrado", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> update(UserDto dto) throws SQLException {
        Optional<User> optionalUser = repository.findById(dto.getId());
        if (!optionalUser.isPresent()) {
            return new ResponseEntity<>(new Message("Usuario no encontrado", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }
        User user = optionalUser.get();
        if (dto.getRoles().isEmpty()) {
            return new ResponseEntity<>(new Message("No ingresó roles", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        String roles = setRoles(dto.getRoles());

        if (user.getPassword() == null) {
            user.setChangePassword(false);
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        user.setRoles(roles);
        if(dto.getEmergency() != null){
            user.setEmergency(dto.getEmergency());
        }
        user = repository.saveAndFlush(user);
        if (user == null) {
            return new ResponseEntity<>(new Message("Usuario no modificado", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message(user, "Usuario modificado", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> updateRole(User user) throws JsonProcessingException {
        String roles = setRoles(user.getRoles());
        user.setRoles(roles);
        user = repository.saveAndFlush(user);
        if (user == null) {
            return new ResponseEntity<>(new Message("Roles de usuario no modificados", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message(user, "Roles de usuario modificados", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> changeStatus(UserDto dto) throws SQLException {
        Optional<User> optionalUser = repository.findById(dto.getId());
        if (!optionalUser.isPresent()) {
            return new ResponseEntity<>(new Message("Usuario no encontrado", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }
        User user = optionalUser.get();
        boolean status = user.isStatus();
        user.setStatus(!status);
        user = repository.saveAndFlush(user);
        if (user == null) {
            return new ResponseEntity<>(new Message("Estado del usuario no modificado", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message(user, "Estado del usuario modificado", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Object> getOne(UserDto dto) {
        Optional<User> optionalUser = repository.findById(dto.getId());
        return optionalUser.<ResponseEntity<Object>>map(user -> new ResponseEntity<>(new Message(user, "Usuario encontrado", TypesResponse.SUCCESS), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(new Message("Usuario no encontrado", TypesResponse.WARNING), HttpStatus.NOT_FOUND));
    }

    public String setRoles(List<Role> list) {
        StringBuilder roles = new StringBuilder("[");
        for (Role role : list) {
            Optional<Role> roleOptional = roleService.findById(role.getId());
            if (!roleOptional.isPresent()) {
                continue;
            }
            if (!roleOptional.get().isStatus()) {
                continue;
            }
            roles.append(roleOptional.get()).append(",");
        }
        roles = new StringBuilder(roles.lastIndexOf(",") == roles.length() - 1 ? roles.substring(0, roles.length() - 1) : roles.toString());
        roles.append("]");
        return roles.toString();
    }

    /*****************************************************************************************************************/
    //ACCESS
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> recover(UserDto usuarioDto) {
        Optional<User> usOpt = repository.findFirstByEmail(usuarioDto.getEmail());
        if (!usOpt.isPresent()) {
            return new ResponseEntity<>(new Message("No se encontró el usuario", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }
        User user = usOpt.get();
        if (!user.isStatus()) {
            return new ResponseEntity<>(new Message("Usuario inactivo", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }
        RandomString tickets = new RandomString(5);
        user.setRecuperation(tickets.nextString());
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        date = calendar.getTime();
        user.setDateExpiration(date);
        repository.saveAndFlush(user);
        emailService.sendSimpleMessage(
                user.getEmail(),
                "UCJ | Solicitud de restablecimiento de contraseña",
                        "<style type=\"text/css\">\n" +
                                "    #outlook a {\n" +
                                "      padding: 0;\n" +
                                "    }\n" +
                                "\n" +
                                "    .ExternalClass {\n" +
                                "      width: 100%;\n" +
                                "    }\n" +
                                "\n" +
                                "    .ExternalClass,\n" +
                                "    .ExternalClass p,\n" +
                                "    .ExternalClass span,\n" +
                                "    .ExternalClass font,\n" +
                                "    .ExternalClass td,\n" +
                                "    .ExternalClass div {\n" +
                                "      line-height: 100%;\n" +
                                "    }\n" +
                                "\n" +
                                "    .es-button {\n" +
                                "      mso-style-priority: 100 !important;\n" +
                                "      text-decoration: none !important;\n" +
                                "    }\n" +
                                "\n" +
                                "    a[x-apple-data-detectors] {\n" +
                                "      color: inherit !important;\n" +
                                "      text-decoration: none !important;\n" +
                                "      font-size: inherit !important;\n" +
                                "      font-family: inherit !important;\n" +
                                "      font-weight: inherit !important;\n" +
                                "      line-height: inherit !important;\n" +
                                "    }\n" +
                                "\n" +
                                "    .es-desk-hidden {\n" +
                                "      display: none;\n" +
                                "      float: left;\n" +
                                "      overflow: hidden;\n" +
                                "      width: 0;\n" +
                                "      max-height: 0;\n" +
                                "      line-height: 0;\n" +
                                "      mso-hide: all;\n" +
                                "    }\n" +
                                "\n" +
                                "    .es-button-border:hover a.es-button,\n" +
                                "    .es-button-border:hover button.es-button {\n" +
                                "      background: #ffffff !important;\n" +
                                "    }\n" +
                                "\n" +
                                "    .es-button-border:hover {\n" +
                                "      background: #ffffff !important;\n" +
                                "      border-style: solid solid solid solid !important;\n" +
                                "      border-color: #3d5ca3 #3d5ca3 #3d5ca3 #3d5ca3 !important;\n" +
                                "    }\n" +
                                "\n" +
                                "    @media only screen and (max-width:600px) {\n" +
                                "\n" +
                                "      p,\n" +
                                "      ul li,\n" +
                                "      ol li,\n" +
                                "      a {\n" +
                                "        line-height: 150% !important\n" +
                                "      }\n" +
                                "\n" +
                                "      h1,\n" +
                                "      h2,\n" +
                                "      h3,\n" +
                                "      h1 a,\n" +
                                "      h2 a,\n" +
                                "      h3 a {\n" +
                                "        line-height: 120% !important\n" +
                                "      }\n" +
                                "\n" +
                                "      h1 {\n" +
                                "        font-size: 20px !important;\n" +
                                "        text-align: center\n" +
                                "      }\n" +
                                "\n" +
                                "      h2 {\n" +
                                "        font-size: 16px !important;\n" +
                                "        text-align: left\n" +
                                "      }\n" +
                                "\n" +
                                "      h3 {\n" +
                                "        font-size: 20px !important;\n" +
                                "        text-align: center\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-header-body h1 a,\n" +
                                "      .es-content-body h1 a,\n" +
                                "      .es-footer-body h1 a {\n" +
                                "        font-size: 20px !important\n" +
                                "      }\n" +
                                "\n" +
                                "      h2 a {\n" +
                                "        text-align: left\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-header-body h2 a,\n" +
                                "      .es-content-body h2 a,\n" +
                                "      .es-footer-body h2 a {\n" +
                                "        font-size: 16px !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-header-body h3 a,\n" +
                                "      .es-content-body h3 a,\n" +
                                "      .es-footer-body h3 a {\n" +
                                "        font-size: 20px !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-menu td a {\n" +
                                "        font-size: 14px !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-header-body p,\n" +
                                "      .es-header-body ul li,\n" +
                                "      .es-header-body ol li,\n" +
                                "      .es-header-body a {\n" +
                                "        font-size: 10px !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-content-body p,\n" +
                                "      .es-content-body ul li,\n" +
                                "      .es-content-body ol li,\n" +
                                "      .es-content-body a {\n" +
                                "        font-size: 16px !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-footer-body p,\n" +
                                "      .es-footer-body ul li,\n" +
                                "      .es-footer-body ol li,\n" +
                                "      .es-footer-body a {\n" +
                                "        font-size: 12px !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-infoblock p,\n" +
                                "      .es-infoblock ul li,\n" +
                                "      .es-infoblock ol li,\n" +
                                "      .es-infoblock a {\n" +
                                "        font-size: 12px !important\n" +
                                "      }\n" +
                                "\n" +
                                "      *[class=\"gmail-fix\"] {\n" +
                                "        display: none !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-m-txt-c,\n" +
                                "      .es-m-txt-c h1,\n" +
                                "      .es-m-txt-c h2,\n" +
                                "      .es-m-txt-c h3 {\n" +
                                "        text-align: center !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-m-txt-r,\n" +
                                "      .es-m-txt-r h1,\n" +
                                "      .es-m-txt-r h2,\n" +
                                "      .es-m-txt-r h3 {\n" +
                                "        text-align: right !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-m-txt-l,\n" +
                                "      .es-m-txt-l h1,\n" +
                                "      .es-m-txt-l h2,\n" +
                                "      .es-m-txt-l h3 {\n" +
                                "        text-align: left !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-m-txt-r img,\n" +
                                "      .es-m-txt-c img,\n" +
                                "      .es-m-txt-l img {\n" +
                                "        display: inline !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-button-border {\n" +
                                "        display: block !important\n" +
                                "      }\n" +
                                "\n" +
                                "      a.es-button,\n" +
                                "      button.es-button {\n" +
                                "        font-size: 14px !important;\n" +
                                "        display: block !important;\n" +
                                "        border-left-width: 0px !important;\n" +
                                "        border-right-width: 0px !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-btn-fw {\n" +
                                "        border-width: 10px 0px !important;\n" +
                                "        text-align: center !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-adaptive table,\n" +
                                "      .es-btn-fw,\n" +
                                "      .es-btn-fw-brdr,\n" +
                                "      .es-left,\n" +
                                "      .es-right {\n" +
                                "        width: 100% !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-content table,\n" +
                                "      .es-header table,\n" +
                                "      .es-footer table,\n" +
                                "      .es-content,\n" +
                                "      .es-footer,\n" +
                                "      .es-header {\n" +
                                "        width: 100% !important;\n" +
                                "        max-width: 600px !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-adapt-td {\n" +
                                "        display: block !important;\n" +
                                "        width: 100% !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .adapt-img {\n" +
                                "        width: 100% !important;\n" +
                                "        height: auto !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-m-p0 {\n" +
                                "        padding: 0px !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-m-p0r {\n" +
                                "        padding-right: 0px !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-m-p0l {\n" +
                                "        padding-left: 0px !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-m-p0t {\n" +
                                "        padding-top: 0px !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-m-p0b {\n" +
                                "        padding-bottom: 0 !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-m-p20b {\n" +
                                "        padding-bottom: 20px !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-mobile-hidden,\n" +
                                "      .es-hidden {\n" +
                                "        display: none !important\n" +
                                "      }\n" +
                                "\n" +
                                "      tr.es-desk-hidden,\n" +
                                "      td.es-desk-hidden,\n" +
                                "      table.es-desk-hidden {\n" +
                                "        width: auto !important;\n" +
                                "        overflow: visible !important;\n" +
                                "        float: none !important;\n" +
                                "        max-height: inherit !important;\n" +
                                "        line-height: inherit !important\n" +
                                "      }\n" +
                                "\n" +
                                "      tr.es-desk-hidden {\n" +
                                "        display: table-row !important\n" +
                                "      }\n" +
                                "\n" +
                                "      table.es-desk-hidden {\n" +
                                "        display: table !important\n" +
                                "      }\n" +
                                "\n" +
                                "      td.es-desk-menu-hidden {\n" +
                                "        display: table-cell !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-menu td {\n" +
                                "        width: 1% !important\n" +
                                "      }\n" +
                                "\n" +
                                "      table.es-table-not-adapt,\n" +
                                "      .esd-block-html table {\n" +
                                "        width: auto !important\n" +
                                "      }\n" +
                                "\n" +
                                "      table.es-social {\n" +
                                "        display: inline-block !important\n" +
                                "      }\n" +
                                "\n" +
                                "      table.es-social td {\n" +
                                "        display: inline-block !important\n" +
                                "      }\n" +
                                "\n" +
                                "      .es-desk-hidden {\n" +
                                "        display: table-row !important;\n" +
                                "        width: auto !important;\n" +
                                "        overflow: visible !important;\n" +
                                "        max-height: inherit !important\n" +
                                "      }\n" +
                                "    }\n" +
                                "  </style>" +
                        "<table class=\"es-wrapper\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"\n" +
                        "      style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top;background-color:#FAFAFA\">\n" +
                        "      <tr style=\"border-collapse:collapse\">\n" +
                        "        <td valign=\"top\" style=\"padding:0;Margin:0\">\n" +
                        "          <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-content\" align=\"center\"\n" +
                        "            style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\">\n" +
                        "            <tr style=\"border-collapse:collapse\">\n" +
                        "              <td class=\"es-adaptive\" align=\"center\" style=\"padding:0;Margin:0\">\n" +
                        "                <table class=\"es-content-body\"\n" +
                        "                  style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;width:600px\"\n" +
                        "                  cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\">\n" +
                        "                  <tr style=\"border-collapse:collapse\">\n" +
                        "                    <td align=\"left\" style=\"padding:10px;Margin:0\">\n" +
                        "                      <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"\n" +
                        "                        style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                        "                        <tr style=\"border-collapse:collapse\">\n" +
                        "                          <td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:580px\">\n" +
                        "                            <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\"\n" +
                        "                              style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                        "                              <tr style=\"border-collapse:collapse\">\n" +
                        "                                <td align=\"center\" class=\"es-infoblock\"\n" +
                        "                                  style=\"padding:0;Margin:0;line-height:14px;font-size:12px;color:#CCCCCC\">\n" +
                        "                            \n" +
                        "                                </td>\n" +
                        "                              </tr>\n" +
                        "                            </table>\n" +
                        "                          </td>\n" +
                        "                        </tr>\n" +
                        "                      </table>\n" +
                        "                    </td>\n" +
                        "                  </tr>\n" +
                        "                </table>\n" +
                        "              </td>\n" +
                        "            </tr>\n" +
                        "          </table>\n" +
                        "          <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-header\" align=\"center\"\n" +
                        "            style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:transparent;background-repeat:repeat;background-position:center top\">\n" +
                        "            <tr style=\"border-collapse:collapse\">\n" +
                        "              <td class=\"es-adaptive\" align=\"center\" style=\"padding:0;Margin:0\">\n" +
                        "                <table class=\"es-header-body\"\n" +
                        "                  style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#3d5ca3;width:600px\"\n" +
                        "                  cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#3d5ca3\" align=\"center\">\n" +
                        "                  <tr style=\"border-collapse:collapse\">\n" +
                        "                    <td\n" +
                        "                      style=\"Margin:0;padding-top:20px;padding-bottom:20px;padding-left:20px;padding-right:20px;background-color:#963412\"\n" +
                        "                      bgcolor=\"#963412\" align=\"left\">\n" +
                        "                      <table cellspacing=\"0\" cellpadding=\"0\" width=\"100%\"\n" +
                        "                        style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                        "                        <tr style=\"border-collapse:collapse\">\n" +
                        "                          <td align=\"left\" style=\"padding:0;Margin:0;width:560px\">\n" +
                        "                            <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\"\n" +
                        "                              style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                        "                              <tr style=\"border-collapse:collapse\">\n" +
                        "                                <td class=\"es-m-p0l es-m-txt-c\" align=\"left\" style=\"padding:0;Margin:0;font-size:0px\"><a\n" +
                        "                                    href=\"https://ucj.edu.mx/\" target=\"_blank\"\n" +
                        "                                    style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#1376C8;font-size:14px\"><img\n" +
                        "                                      src=\"https://qcwnun.stripocdn.email/content/guids/af08c7ef-82c5-4a6e-bd81-1c62a9c60f97/images/logo.png\"\n" +
                        "                                      alt\n" +
                        "                                      style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"\n" +
                        "                                      height=\"63\" width=\"261\"></a></td>\n" +
                        "                              </tr>\n" +
                        "                            </table>\n" +
                        "                          </td>\n" +
                        "                        </tr>\n" +
                        "                      </table>\n" +
                        "                    </td>\n" +
                        "                  </tr>\n" +
                        "                </table>\n" +
                        "              </td>\n" +
                        "            </tr>\n" +
                        "          </table>\n" +
                        "          <table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"\n" +
                        "            style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\">\n" +
                        "            <tr style=\"border-collapse:collapse\">\n" +
                        "              <td style=\"padding:0;Margin:0;background-color:#fafafa\" bgcolor=\"#fafafa\" align=\"center\">\n" +
                        "                <table class=\"es-content-body\"\n" +
                        "                  style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#ffffff;width:600px\"\n" +
                        "                  cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\">\n" +
                        "                  <tr style=\"border-collapse:collapse\">\n" +
                        "                    <td\n" +
                        "                      style=\"padding:0;Margin:0;padding-left:20px;padding-right:20px;padding-top:40px;background-color:transparent\"\n" +
                        "                      bgcolor=\"transparent\" align=\"left\">\n" +
                        "                      <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"\n" +
                        "                        style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                        "                        <tr style=\"border-collapse:collapse\">\n" +
                        "                          <td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:560px\">\n" +
                        "                            <table\n" +
                        "                              style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-position:left top\"\n" +
                        "                              width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\">\n" +
                        "                              <tr style=\"border-collapse:collapse\">\n" +
                        "                                <td align=\"center\"\n" +
                        "                                  style=\"padding:0;Margin:0;padding-top:5px;padding-bottom:5px;font-size:0px\"><img\n" +
                        "                                    src=\"https://qcwnun.stripocdn.email/content/guids/af08c7ef-82c5-4a6e-bd81-1c62a9c60f97/images/undraw_forgot_password_re_hxwm.png\"\n" +
                        "                                    alt\n" +
                        "                                    style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"\n" +
                        "                                    width=\"200\" height=\"133\"></td>\n" +
                        "                              </tr>\n" +
                        "                              <tr style=\"border-collapse:collapse\">\n" +
                        "                                <td align=\"center\" style=\"padding:0;Margin:0;padding-top:15px;padding-bottom:15px\">\n" +
                        "                                  <h1\n" +
                        "                                    style=\"Margin:0;line-height:24px;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;font-size:20px;font-style:normal;font-weight:normal;color:#333333\">\n" +
                        "                                    <b>Restablecimiento de contraseña</b></h1>\n" +
                        "                                </td>\n" +
                        "                              </tr>\n" +
                        "                              <tr style=\"border-collapse:collapse\">\n" +
                        "                                <td align=\"center\" style=\"padding:0;Margin:0;padding-left:40px;padding-right:40px\">\n" +
                        "                                  <p\n" +
                        "                                    style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;line-height:24px;color:#666666;font-size:16px\">\n" +
                        "                                    Para restablecer la contraseña debes ingresar el siguiente código en la página que\n" +
                        "                                    lo solicita:</p>\n" +
                        "                                </td>\n" +
                        "                              </tr>\n" +
                        "                              <tr style=\"border-collapse:collapse\">\n" +
                        "                                <td align=\"center\"\n" +
                        "                                  style=\"Margin:0;padding-top:20px;padding-bottom:20px;padding-left:40px;padding-right:40px\">\n" +
                        "                                  <p\n" +
                        "                                    style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;line-height:27px;color:#8b4513;font-size:18px\">\n" +
                        "                                    <strong>"+user.getRecuperation()+"</strong></p>\n" +
                        "                                </td>\n" +
                        "                              </tr>\n" +
                        "                              <tr style=\"border-collapse:collapse\">\n" +
                        "                                <td align=\"center\"\n" +
                        "                                  style=\"Margin:0;padding-top:25px;padding-bottom:30px;padding-left:40px;padding-right:40px\">\n" +
                        "                                  <p\n" +
                        "                                    style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;line-height:24px;color:#666666;font-size:16px\">\n" +
                        "                                    <strong>Nota: El código proporcionado tiene una validez de 24 horas.</strong></p>\n" +
                        "                                </td>\n" +
                        "                              </tr>\n" +
                        "                            </table>\n" +
                        "                          </td>\n" +
                        "                        </tr>\n" +
                        "                      </table>\n" +
                        "                    </td>\n" +
                        "                  </tr>\n" +
                        "                </table>\n" +
                        "              </td>\n" +
                        "            </tr>\n" +
                        "          </table>\n" +
                        "          <table class=\"es-footer\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"\n" +
                        "            style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:transparent;background-repeat:repeat;background-position:center top\">\n" +
                        "            <tr style=\"border-collapse:collapse\">\n" +
                        "              <td style=\"padding:0;Margin:0;background-color:#fafafa\" bgcolor=\"#fafafa\" align=\"center\">\n" +
                        "                <table class=\"es-footer-body\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\"\n" +
                        "                  style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;width:600px\">\n" +
                        "                  <tr style=\"border-collapse:collapse\">\n" +
                        "                    <td\n" +
                        "                      style=\"Margin:0;padding-top:10px;padding-left:20px;padding-right:20px;padding-bottom:30px;background-color:#963412\"\n" +
                        "                      bgcolor=\"#963412\" align=\"left\">\n" +
                        "                      <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"\n" +
                        "                        style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                        "                        <tr style=\"border-collapse:collapse\">\n" +
                        "                          <td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:560px\">\n" +
                        "                            <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"\n" +
                        "                              style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                        "                              <tr style=\"border-collapse:collapse\">\n" +
                        "                                <td align=\"center\" style=\"padding:0;Margin:0;display:none\"></td>\n" +
                        "                              </tr>\n" +
                        "                            </table>\n" +
                        "                          </td>\n" +
                        "                        </tr>\n" +
                        "                      </table>\n" +
                        "                    </td>\n" +
                        "                  </tr>\n" +
                        "                </table>\n" +
                        "              </td>\n" +
                        "            </tr>\n" +
                        "          </table>\n" +
                        "        </td>\n" +
                        "      </tr>\n" +
                        "    </table>\n" +
                        "  </div>");

        return new ResponseEntity<>(new Message(user, "Correo enviado", TypesResponse.SUCCESS), HttpStatus.OK);

    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> verifyCode(UserDto dto) {
        Optional<User> usOpt = repository.findFirstByEmail(dto.getEmail());
        if (!usOpt.isPresent()) {
            return new ResponseEntity<>(new Message("Ocurrió un TypesResponse.ERROR", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }
        if (!usOpt.get().isStatus()) {
            return new ResponseEntity<>(new Message("Usuario inhabilitado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        User user = usOpt.get();
        if (!dto.getRecuperation().equals(user.getRecuperation())) {
            return new ResponseEntity<>(new Message("El código no coincide con el que fue enviado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (user.getDateExpiration().before(new Date())) {
            user.setRecuperation(null);
            user.setDateExpiration(null);
            repository.saveAndFlush(user);
            return new ResponseEntity<>(new Message("Código caducado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message(user, "Código válido", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> updatePassword(UserDto dto) {
        Optional<User> usOpt = repository.findFirstByEmail(dto.getEmail());
        if (!usOpt.isPresent()) {
            return new ResponseEntity<>(new Message("Falló el restablecimiento de contraseña", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }
        if (this.verifyCode(dto).getStatusCodeValue() != 200) {
            return new ResponseEntity<>(new Message("Ocurrió un problema con el código", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (passwordEncoder.matches(dto.getPassword(), usOpt.get().getPassword())) {
            return new ResponseEntity<>(new Message("La nueva contraseña no puede ser igual a la actual", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        boolean valid = passwordValidator.isValid(dto.getPassword());
        if (!valid) {
            return new ResponseEntity<>(new Message("La contraseña no cumple con las características de contraseña segura", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        User user = usOpt.get();
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setChangePassword(false);
        user.setRecuperation(null);
        user.setDateExpiration(null);
        user = repository.saveAndFlush(user);
        if (user == null) {
            return new ResponseEntity<>(new Message("No se modificó la contraseña", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message(user, "Contraseña modificada", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> restorePassword(UserDto dto) {
        Optional<User> usOpt = repository.findById(dto.getId());
        if (!usOpt.isPresent()) {
            return new ResponseEntity<>(new Message("No se encontró el usuario", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }
        if (!usOpt.get().isStatus()) {
            return new ResponseEntity<>(new Message("El usuario está inhabilitado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        User user = usOpt.get();
        user.setPassword(passwordEncoder.encode(user.getEmail()));
        user.setChangePassword(true);
        user = repository.saveAndFlush(user);
        if (user == null) {
            return new ResponseEntity<>(new Message("No se modificó la contraseña", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message(user, "Contraseña restablecida", TypesResponse.SUCCESS), HttpStatus.OK);
    }


    @Transactional(rollbackFor = {SQLException.class})
    public void setAttempts(Long id) {
        Optional<User> optionalUser = repository.findById(id);
        if (!optionalUser.isPresent()) {
            return;
        }
        User user = optionalUser.get();
        user.setAttempts(user.getAttempts() + 1);
        if (user.getAttempts() >= 4) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            user.setTimeBlocked(calendar.getTime());
        }
        user = repository.saveAndFlush(user);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public boolean restore(Long id) {
        Optional<User> optionalUser = repository.findById(id);
        if (!optionalUser.isPresent()) {
            return false;
        }
        User user = optionalUser.get();
        user.setAttempts(0);
        user.setTimeBlocked(null);
        user.setStatus(true);
        user = repository.saveAndFlush(user);
        return (user == null);
    }

}