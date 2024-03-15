package mx.edu.utez.sigebe.access.role.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladmihalcea.hibernate.type.json.JsonType;
import mx.edu.utez.sigebe.access.privilege.model.Privilege;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "roles")
@TypeDef(name = "json", typeClass = JsonType.class)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "key_role", columnDefinition = "VARCHAR(10)")
    private String keyRole;
    @Column(name = "name", columnDefinition = "VARCHAR(50)")
    private String name;
    @Column(name = "privileges", columnDefinition = "json")
    @Type(type = "json")
    private String privileges;
    @Column(name = "status", columnDefinition = "BOOL DEFAULT TRUE")
    private boolean status;
    @Column(name = "create_at", insertable = false, columnDefinition = "TIMESTAMP DEFAULT NOW()")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public Role() {
    }

    public Role(String name, String keyRole, String privileges, boolean status) {
        this.name = name;
        this.status = status;
        this.keyRole = keyRole;
        this.privileges = privileges;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String role) {
        this.name = role;
    }

    public String getKeyRole() {
        return keyRole;
    }

    public void setKeyRole(String keyRole) {
        this.keyRole = keyRole;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Privilege> getPrivileges() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<Privilege> privileges = mapper.readValue(this.privileges, new TypeReference<List<Privilege>>() {
        });
        return privileges;
    }

    public void setPrivileges(List<Privilege> privileges) {
        this.privileges = privileges.toString();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\": \"" + id +
                "\", \"keyRole\": \"" + keyRole +
                "\", \"name\": \"" + name +
                "\", \"privileges\": " + privileges +
                "}";
    }

    public void asignValues(RoleDto dto) {
        this.keyRole = dto.getKeyRole();
        this.name = dto.getName();
    }

    public void asignValuesRegister(RoleDto dto) {
        asignValues(dto);
        this.status = true;
    }
}
