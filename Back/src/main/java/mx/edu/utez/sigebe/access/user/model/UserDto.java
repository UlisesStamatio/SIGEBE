package mx.edu.utez.sigebe.access.user.model;

import mx.edu.utez.sigebe.access.role.model.Role;
import mx.edu.utez.sigebe.basecatalog.person.model.Person;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

public class UserDto {
    @NotNull(groups = {Modify.class, Restore.class, ChangeStatus.class})
    private Long id;
    @NotBlank(groups = {Modify.class, Register.class, Recover.class, ChangePassword.class, VerifyCode.class, UpdatePassword.class})
    private String email;
    @NotBlank(groups = {ChangePassword.class, UpdatePassword.class})
    private String password;
    private String emergency;
    @NotNull(groups = {Modify.class, Register.class, ChangeRole.class})
    private List<Role> roles;
    @NotBlank(groups = {UpdatePassword.class})
    private String passwordNew;
    @NotBlank(groups = {VerifyCode.class, ChangePassword.class})
    private String recuperation;
    private boolean changePassword;
    private Date dateExpiration;
    private boolean status;

    public UserDto() {
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

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
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

    public String getPasswordNew() {
        return passwordNew;
    }

    public void setPasswordNew(String passwordNew) {
        this.passwordNew = passwordNew;
    }


    public String getEmergency() {
        return emergency;
    }

    public void setEmergency(String emergency) {
        this.emergency = emergency;
    }

    public interface Register {
    }

    public interface Modify {
    }

    public interface Recover {
    }

    public interface ChangePassword {
    }

    public interface VerifyCode {
    }

    public interface UpdatePassword {
    }

    public interface ChangeStatus {
    }

    public interface Restore {
    }

    public interface ChangeRole {
    }

    public interface Find{

    }

}
