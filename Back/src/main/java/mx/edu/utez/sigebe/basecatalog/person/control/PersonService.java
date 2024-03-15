package mx.edu.utez.sigebe.basecatalog.person.control;

import mx.edu.utez.sigebe.basecatalog.address.control.AddressService;
import mx.edu.utez.sigebe.basecatalog.person.model.Person;
import mx.edu.utez.sigebe.basecatalog.person.model.PersonDto;
import mx.edu.utez.sigebe.basecatalog.person.model.PersonRepository;
import mx.edu.utez.sigebe.utils.entity.Message;
import mx.edu.utez.sigebe.utils.entity.TypesResponse;
import mx.edu.utez.sigebe.utils.validator.CurpValidator;
import mx.edu.utez.sigebe.utils.validator.PhoneValidator;
import mx.edu.utez.sigebe.utils.validator.RfcValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

@Transactional
@Service
public class PersonService {
    private final static Logger logger = LoggerFactory.getLogger(PersonService.class);

    private final PersonRepository repository;
    private final RfcValidator rfcValidator;
    private final CurpValidator curpValidator;
    private final PhoneValidator phoneValidator;
    private final AddressService addressService;
    @Autowired
    public PersonService(PersonRepository repository, RfcValidator rfcValidator, CurpValidator curpValidator, PhoneValidator phoneValidator,AddressService addressService) {
        this.repository = repository;
        this.rfcValidator = rfcValidator;
        this.curpValidator = curpValidator;
        this.phoneValidator = phoneValidator;
        this.addressService = addressService;
    }

    @Transactional(readOnly = true)
    public Optional<Person> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Person> findByName(String name) {
        return repository.findByName(name);
    }

    @Transactional(readOnly = true)
    public ResponseEntity findAll() {
        return new ResponseEntity<>(new Message(repository.findAll(),
                "Lista de las personas", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity findAllEnabled() {
        return new ResponseEntity<>(new Message(repository.findAllByStatus(true),
                "Lista de las personas activas", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity save(PersonDto dto) throws SQLException {
        dto.setCurp(dto.getCurp().toUpperCase());
        dto.setSex(dto.getSex().toUpperCase());

        Optional<Person> optCurp = repository.searchByCurpAndId(dto.getCurp(), 0L);
        if (optCurp.isPresent()) {
            return new ResponseEntity<>(new Message("El CURP ya existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getRfc() != null && dto.getRfc() != "") {
            dto.setRfc(dto.getRfc().toUpperCase());
            Optional<Person> optRfc = repository.searchByRfcAndId(dto.getRfc(), 0L);
            if (optRfc.isPresent()) {
                return new ResponseEntity<>(new Message("El RFC ya existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }
            if (!rfcValidator.isValid(dto.getRfc())) {
                return new ResponseEntity<>(new Message("RFC malformado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }
        }
        if (!curpValidator.isValid(dto.getCurp())) {
            return new ResponseEntity<>(new Message("CURP malformado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (!phoneValidator.isValid(dto.getPhone())) {
            return new ResponseEntity<>(new Message("Número de teléfono de casa malformado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getCellphone() != null) {
            if (!phoneValidator.isValid(dto.getCellphone())) {
                return new ResponseEntity<>(new Message("Celular malformado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }
        }
        Person person = new Person();
        if(dto.getNss() !=null){
            person.setNss(dto.getNss());
        }
        if(dto.getAddress() != null){
            dto.getAddress().setTown(dto.getAddress().getTown());
            Message message = addressService.save(dto.getAddress());
            if(message.getType() == TypesResponse.ERROR){
                return new ResponseEntity<>(message,HttpStatus.BAD_REQUEST);
            }
            person.setAddress(dto.getAddress());
        }
        person.asignValuesRegister(dto);
        person.setStatus(true);
        person = repository.saveAndFlush(person);
        if (person == null) {
            return new ResponseEntity<>(new Message("Persona no registrada", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message(person, "Persona registrada", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity update(PersonDto dto) throws SQLException {
        Optional<Person> optionalPerson = repository.findById(dto.getId());
        if (!optionalPerson.isPresent()) {
            return new ResponseEntity<>(new Message("Persona no encontrada", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }

        if(Objects.equals(dto.getName(), "") || Objects.equals(dto.getSurname(), "")){
            return new ResponseEntity<>(new Message("El nombre y/o los apellidos no deben estar vacíos", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }

        dto.setCurp(dto.getCurp().toUpperCase());
        dto.setSex(dto.getSex().toUpperCase());
        Person person = optionalPerson.get();
        Optional<Person> optCurp = repository.searchByCurpAndId(dto.getCurp(), dto.getId());
        if (optCurp.isPresent()) {
            return new ResponseEntity<>(new Message("El CURP ya existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getRfc() != null  && dto.getRfc() != "") {
            dto.setRfc(dto.getRfc().toUpperCase());
            Optional<Person> optRfc = repository.searchByRfcAndId(dto.getRfc(), dto.getId());
            if (optRfc.isPresent()) {
                return new ResponseEntity<>(new Message("El RFC ya existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }
            if (!rfcValidator.isValid(dto.getRfc())) {
                return new ResponseEntity<>(new Message("RFC malformado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }
        }
        if (!curpValidator.isValid(dto.getCurp())) {
            return new ResponseEntity<>(new Message("CURP malformado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (!phoneValidator.isValid(dto.getPhone())) {
            return new ResponseEntity<>(new Message("Número de teléfono de casa malformado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getCellphone() != null) {
            if (!phoneValidator.isValid(dto.getCellphone())) {
                return new ResponseEntity<>(new Message("Celular malformado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }
        }
        if(dto.getNss() !=null){
            person.setNss(dto.getNss());
        }
        if(dto.getAddress() != null){
            if(dto.getAddress().getId()==null){
                return new ResponseEntity<>(new Message("La dirección no tiene identificador", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
            }

            dto.getAddress().setTown("");
            Message message = addressService.update(dto.getAddress());
            if(message.getType() == TypesResponse.ERROR){
                return new ResponseEntity<>(message,HttpStatus.BAD_REQUEST);
            }
            person.setAddress(dto.getAddress());
        }
        person.asignValuesModify(dto);
        person = repository.saveAndFlush(person);
        if (person == null) {
            return new ResponseEntity<>(new Message("Persona no modificada", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message(person, "Persona modificada", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity changeStatus(PersonDto dto) throws SQLException {
        Optional<Person> optionalPerson = repository.findById(dto.getId());
        if (!optionalPerson.isPresent()) {
            return new ResponseEntity<>(new Message("Persona no encontrada", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }
        Person person = optionalPerson.get();
        boolean status = person.isStatus();
        person.setStatus(!status);
        person = repository.saveAndFlush(person);
        if (person == null) {
            return new ResponseEntity<>(new Message("Estado de la persona no modificado", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message(person, "Estado de la persona modificado", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public Person saveInitialPerson(Person person) {
        return repository.saveAndFlush(person);
    }
}