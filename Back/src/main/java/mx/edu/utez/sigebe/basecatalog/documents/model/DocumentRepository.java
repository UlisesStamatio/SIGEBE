package mx.edu.utez.sigebe.basecatalog.documents.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document,Long> {
    List<Document> findAllByStatusIsTrue();

    @Query(value = "SELECT * " + "FROM documents " + "WHERE name LIKE ?1 " + "AND id != ?2 " + "LIMIT 1", nativeQuery = true)
    Optional<Document> searchByNameAndId(String name, Long id);
}
