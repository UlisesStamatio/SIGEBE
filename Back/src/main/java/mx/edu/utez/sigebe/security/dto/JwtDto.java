package mx.edu.utez.sigebe.security.dto;

import mx.edu.utez.sigebe.access.role.model.Role;

import java.util.List;

public class JwtDto {
    private String token;
    private String bearer = "Bearer";
    private String email;
    private String name;
    private Long identKey;
    private List<Role> roles;

    public JwtDto(String token, String email, List<Role> roles) {
        this.token = token;
        this.email = email;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBearer() {
        return bearer;
    }

    public void setBearer(String bearer) {
        this.bearer = bearer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getIdentKey() {
        return identKey;
    }

    public void setIdentKey(Long identKey) {
        this.identKey = identKey;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
