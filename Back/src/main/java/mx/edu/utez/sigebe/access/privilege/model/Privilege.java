package mx.edu.utez.sigebe.access.privilege.model;

import javax.persistence.*;

@Entity
@Table(name = "privilegies")
public class Privilege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "name", columnDefinition = "VARCHAR(50)")
    private PrivilegeName name;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "type", columnDefinition = "VARCHAR(50)")
    private String type;

    public Privilege() {
    }

    public Privilege(PrivilegeName name, String description) {
        this.name = name;
        this.description = description;
    }

    public Privilege(PrivilegeName name, String description, String type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String role) {
        this.description = role;
    }

    public PrivilegeName getName() {
        return name;
    }

    public void setName(PrivilegeName code) {
        this.name = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\": \"" + id +
                "\", \"name\": \"" + name +
                "\", \"description\": \"" + description +
                "\", \"type\": \"" + type +
                "\"}";
    };
}
