package mx.edu.utez.sigebe.access.role.model;

import mx.edu.utez.sigebe.access.privilege.model.Privilege;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class RoleDto {
    @NotNull(groups = {Modify.class, ChangeStatus.class})
    private Long id;
    @NotBlank(groups = {Modify.class, Register.class})
    private String name;
    @NotBlank(groups = {Modify.class, Register.class})
    private String keyRole;
    @NotNull(groups = {Modify.class, Register.class})
    private List<Privilege> privileges;
    private boolean status;

    public RoleDto() {
    }

    public RoleDto(Role name) {
        this.status = name.isStatus();
        this.name = name.getName();
        this.id = name.getId();
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

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getKeyRole() {
        return keyRole;
    }

    public void setKeyRole(String keyRole) {
        this.keyRole = keyRole;
    }

    public List<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<Privilege> privileges) {
        this.privileges = privileges;
    }

    public interface Register {
    }

    public interface Modify {
    }

    public interface ChangeStatus {
    }
}
