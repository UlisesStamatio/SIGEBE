package mx.edu.utez.sigebe.access.privilege.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Optional<Privilege> findFirstByName(PrivilegeName name);
}
