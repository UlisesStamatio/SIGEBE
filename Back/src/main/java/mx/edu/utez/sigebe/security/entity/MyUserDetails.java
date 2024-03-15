package mx.edu.utez.sigebe.security.entity;

import mx.edu.utez.sigebe.access.privilege.model.Privilege;
import mx.edu.utez.sigebe.access.role.model.Role;
import mx.edu.utez.sigebe.access.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyUserDetails implements UserDetails {

    private final static Logger logger = LoggerFactory.getLogger(MyUserDetails.class);
    private final String username;
    private final String password;
    private final boolean blocked;
    private final boolean enabled;

    private final Collection<? extends GrantedAuthority> authorities;

    public MyUserDetails(String username, String password, boolean blocked, boolean enabled, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.blocked = blocked;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    public static MyUserDetails build(User user) throws Exception {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : user.getRoles()) {
            for (Privilege privilege : role.getPrivileges()) {
                authorities.add(new SimpleGrantedAuthority(privilege.getName().name()));
            }
        }
        return new MyUserDetails(user.getEmail(), user.getPassword(), user.isStatus(), user.isStatus(), authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return blocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
