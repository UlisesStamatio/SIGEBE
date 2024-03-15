package mx.edu.utez.sigebe.basecatalog.documents.control;

import mx.edu.utez.sigebe.access.privilege.model.Privilege;
import mx.edu.utez.sigebe.basecatalog.documents.model.Document;
import mx.edu.utez.sigebe.basecatalog.documents.model.DocumentDto;
import mx.edu.utez.sigebe.basecatalog.documents.model.DocumentRepository;
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
public class DocumentService {
    private final static Logger logger = LoggerFactory.getLogger(DocumentService.class);

    private final DocumentRepository repository;

    @Autowired
    public DocumentService(DocumentRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Object> findAll() {
        return new ResponseEntity<>(new Message(repository.findAll(), "Listado de documentos", TypesResponse.SUCCESS), HttpStatus.OK);
    }


    @Transactional(readOnly = true)
    public ResponseEntity<Object> findAllEnabled() {
        return new ResponseEntity<>(new Message(repository.findAllByStatusIsTrue(), "Listado de documentos activos", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> save(DocumentDto dto) {

        Optional<Document> optional = repository.searchByNameAndId(dto.getName(),0L);
        if (optional.isPresent()){
            return new ResponseEntity<>(new Message("El nombre del documento ya existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }

        Document document = new Document();
        document.asignValues(dto);
        document.setStatus(true);

        document = repository.saveAndFlush(document);

        if (document == null) {
            return new ResponseEntity<>(new Message("Documento no registrado", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message(document, "Documento registrado", TypesResponse.SUCCESS), HttpStatus.OK);
    }


    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> update(DocumentDto dto) {

        Optional<Document> documentOptional = repository.findById(dto.getId());
        if (!documentOptional.isPresent()){
            return new ResponseEntity<>(new Message("No se encontró el documento", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);

        }

        Optional<Document> optional = repository.searchByNameAndId(dto.getName(),dto.getId());
        if (optional.isPresent()){
            return new ResponseEntity<>(new Message("El nombre del documento ya existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }

        Document document = documentOptional.get();
        document.asignValues(dto);
        document.setId(dto.getId());


        document = repository.saveAndFlush(document);

        if (document == null) {
            return new ResponseEntity<>(new Message("Documento no modificado", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message(document, "Documento modificado", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Object> changeStatus(DocumentDto dto) {
        Optional<Document> optional = repository.findById(dto.getId());
        if (!optional.isPresent()) {
            return new ResponseEntity<>(new Message("No se encontró el Documento", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }
        Document document = optional.get();
        document.setStatus(!document.isStatus());
        document = repository.saveAndFlush(document);
        if (document == null) {
            return new ResponseEntity<>(new Message("No se modificó el estado del Documento", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message(document, "Se modificó el estado del Documento", TypesResponse.SUCCESS), HttpStatus.OK);
    }
}
