package mx.edu.utez.sigebe.basecatalog.address.control;

import mx.edu.utez.sigebe.basecatalog.address.model.Address;
import mx.edu.utez.sigebe.basecatalog.address.model.AddressRepository;
import mx.edu.utez.sigebe.utils.entity.Message;
import mx.edu.utez.sigebe.utils.entity.TypesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Optional;

@Service
@Transactional
public class AddressService {
    private final static Logger logger = LoggerFactory.getLogger(AddressService.class);
    @Autowired
    AddressRepository repository;

    @Transactional(readOnly = true)
    public Optional<Address> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public Message save(Address address) {
        address.setStatus(true);
        address = repository.saveAndFlush(address);
        if (address == null) {
            return new Message("No se registró la Dirección", TypesResponse.ERROR);
        }
        return new Message(address, "Se registró la Dirección", TypesResponse.SUCCESS);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public Message update(Address address) {
        Optional<Address> optional = repository.findById(address.getId());
        if (!optional.isPresent()) {
            return new Message("No se encontró la Dirección", TypesResponse.WARNING);
        }
        Address addressUpdated = optional.get();
        addressUpdated.setValues(address);
        addressUpdated.setStatus(true);
        addressUpdated = repository.saveAndFlush(addressUpdated);
        if (addressUpdated == null) {
            return new Message("No se modificó la Dirección", TypesResponse.ERROR);
        }
        return new Message(addressUpdated, "Se modificó la Dirección", TypesResponse.SUCCESS);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public Message changeStatus(Address address) {
        Optional<Address> optional = repository.findById(address.getId());
        if (!optional.isPresent()) {
            return new Message("No se encontró la Dirección", TypesResponse.WARNING);
        }
        address = optional.get();
        address.setStatus(!address.isStatus());
        address = repository.saveAndFlush(address);
        if (address == null) {
            return new Message("No se modificó el estado de la Dirección", TypesResponse.ERROR);
        }
        return new Message(address, "Se modificó el estado de la Dirección", TypesResponse.SUCCESS);
    }
}
