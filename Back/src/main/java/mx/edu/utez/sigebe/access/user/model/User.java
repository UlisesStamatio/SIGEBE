package mx.edu.utez.sigebe.access.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import mx.edu.utez.sigebe.access.role.model.Role;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Email
    @Column(name = "email", columnDefinition = "VARCHAR(50) NOT NULL")
    private String email;
    @Column(name = "password", columnDefinition = "VARCHAR(255) NOT NULL")
    @JsonIgnore
    private String password;

    @Column(name = "emergency", columnDefinition = "VARCHAR(100)")
    private String emergency;
    @Column(name = "recuperation", columnDefinition = "VARCHAR(5)")
    private String recuperation;
    @Column(name = "date_expiration", columnDefinition = "TIMESTAMP NULL")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateExpiration;
    @Column(name = "time_blocked", columnDefinition = "TIMESTAMP NULL")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeBlocked;
    @Column(name = "change_password", columnDefinition = "BOOL DEFAULT FALSE")
    private boolean changePassword;
    @Column(name = "attempts", columnDefinition = "INT DEFAULT 0")
    private int attempts;
    @Column(name = "roles", columnDefinition = "json")
    @Type(type = "json")
    private String roles;
    @Column(name = "create_at", insertable = false, columnDefinition = "TIMESTAMP DEFAULT NOW()")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "status", columnDefinition = "BOOL DEFAULT TRUE")
    private boolean status;


    public User() {
    }

    public User(String email, String password, String roles, String emergency, boolean status) {
        this.email = email;
        this.password = password;
        this.emergency = emergency;
        this.roles = roles;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRecuperation() {
        return recuperation;
    }

    public void setRecuperation(String recuperation) {
        this.recuperation = recuperation;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Role> getRoles() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
        List<Role> roles = mapper.readValue(this.roles, new TypeReference<List<Role>>() {
        });
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }


    public Date getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(Date dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public boolean isChangePassword() {
        return changePassword;
    }

    public void setChangePassword(boolean changePassword) {
        this.changePassword = changePassword;
    }

    public String getEmergency() {
        return emergency;
    }

    public void setEmergency(String emergency) {
        this.emergency = emergency;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public Date getTimeBlocked() {
        return timeBlocked;
    }

    public void setTimeBlocked(Date timeBlocked) {
        this.timeBlocked = timeBlocked;
    }

    public void asignValues(UserDto dto) {
        this.emergency = dto.getEmergency();
    }

    public void asignValuesRegister(UserDto dto) {
        asignValues(dto);
        this.email = dto.getEmail();
        this.password = dto.getEmail();
        this.status = true;
        this.changePassword = true;
    }

    public void asignValuesModify(UserDto dto) {
        asignValues(dto);
        this.status = dto.isStatus();
        this.changePassword = dto.isChangePassword();
        this.password = dto.getEmail();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", emergency='" + emergency + '\'' +
                ", recuperation='" + recuperation + '\'' +
                ", dateExpiration=" + dateExpiration +
                ", timeBlocked=" + timeBlocked +
                ", changePassword=" + changePassword +
                ", attempts=" + attempts +
                ", roles='" + roles + '\'' +
                ", createdAt=" + createdAt +
                ", status=" + status +
                '}';
    }
}
