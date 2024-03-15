package mx.edu.utez.sigebe.access.visualconfig.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VisualConfigRepository extends JpaRepository<VisualConfig, Long> {

}
