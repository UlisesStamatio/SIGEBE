package mx.edu.utez.sigebe.access.role.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findRoleByKeyRole(String keyRole);

    List<Role> findAllByStatus(boolean status);

    @Query(value = "SELECT * " +
            "FROM roles " +
            "WHERE name LIKE ?1 " +
            "AND id != ?2 " +
            "LIMIT 1", nativeQuery = true)
    Optional<Role> searchByNameAndId(String name, long id);

    @Query(value = "SELECT * " +
            "FROM roles " +
            "WHERE key_role LIKE ?1 " +
            "AND id != ?2 " +
            "LIMIT 1", nativeQuery = true)
    Optional<Role> searchByKeyRoleAndId(String keyRole, long id);

}
