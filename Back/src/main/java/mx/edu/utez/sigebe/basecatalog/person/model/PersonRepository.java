package mx.edu.utez.sigebe.basecatalog.person.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findAllByStatus(boolean state);

    Optional<Person> findByName(String name);
    @Query(value = "SELECT * " +
            "FROM people " +
            "WHERE curp LIKE ?1 " +
            "AND id != ?2 " +
            "LIMIT 1", nativeQuery = true)

    Optional<Person> searchByCurpAndId(String curp, Long id);

    @Query(value = "SELECT * " +
            "FROM people " +
            "WHERE rfc LIKE ?1 " +
            "AND id != ?2 " +
            "LIMIT 1", nativeQuery = true)
    Optional<Person> searchByRfcAndId(String rfc, Long id);
}
