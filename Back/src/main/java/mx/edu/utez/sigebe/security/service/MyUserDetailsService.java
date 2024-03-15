package mx.edu.utez.sigebe.security.service;

import mx.edu.utez.sigebe.access.user.control.UserService;
import mx.edu.utez.sigebe.access.user.model.User;
import mx.edu.utez.sigebe.security.entity.MyUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private final static Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

    private final UserService userService;

    @Autowired
    @Lazy
    public MyUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            Optional<User> userOptional = userService.findFirstByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                String roles = userService.setRoles(user.getRoles());
                user.setRoles(roles);
                return MyUserDetails.build(user);
            }
            logger.error("NO SE ENCONTRÓ");
            throw new RuntimeException("NO SE ENCONTRÓ");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
