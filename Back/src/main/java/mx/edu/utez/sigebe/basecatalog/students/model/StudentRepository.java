package mx.edu.utez.sigebe.basecatalog.students.model;

import mx.edu.utez.sigebe.basecatalog.documents.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student,Long> {
    Optional<Student> findFirstByEnrollment(String enrollment);
    List<Student> findAllByStatusIsTrue();

    @Query(value = "SELECT * " + "FROM students " + "WHERE enrollment LIKE ?1 " + "AND id != ?2 " + "LIMIT 1", nativeQuery = true)
    Optional<Student> searchByEnrollmentAndId(String name, Long id);
}
