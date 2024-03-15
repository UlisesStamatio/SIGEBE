package mx.edu.utez.sigebe.security.control;

import mx.edu.utez.sigebe.access.privilege.model.PrivilegeName;
import mx.edu.utez.sigebe.security.jwt.JwtEntryPoint;
import mx.edu.utez.sigebe.security.jwt.JwtTokenFilter;
import mx.edu.utez.sigebe.security.service.MyUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MainSecurity extends WebSecurityConfigurerAdapter {
    private final static Logger logger = LoggerFactory.getLogger(MainSecurity.class);
    @Autowired
    MyUserDetailsService userDetailsService;

    @Autowired
    JwtEntryPoint entryPoint;

    private static final String[] AUTH_WHITELIST = {
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/v2/api-docs",
            "/webjars/**"
    };

    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeHttpRequests(authorize -> authorize
                        .antMatchers("/api/role/**").hasAuthority(PrivilegeName.ROLES.name())
                        .antMatchers("/api/privilege/**").hasAuthority(PrivilegeName.PRIVILEGIOS.name())
                        .antMatchers("/api/user/**").hasAuthority(PrivilegeName.USUARIOS.name())
                        .antMatchers("/api/visual-config/**").hasAuthority(PrivilegeName.CONFIGURACIONES_VISUALES.name())
                        .antMatchers("/api/state/**").hasAuthority(PrivilegeName.ESTADOS.name())
                        .antMatchers("/api/town/**").hasAuthority(PrivilegeName.MUNICIPIOS.name())

                        .antMatchers("/api/study-level/**").hasAuthority(PrivilegeName.NIVELES_DE_ESTUDIO.name())
                        .antMatchers("/api/dismiss-motive/**").hasAuthority(PrivilegeName.MOTIVOS_DE_BAJAS.name())
                        .antMatchers("/api/payment-concept/**").hasAuthority(PrivilegeName.CONCEPTOS_DE_PAGO.name())
                        .antMatchers("/api/scholarship-type/**").hasAuthority(PrivilegeName.TIPOS_DE_BECA.name())
                        .antMatchers("/api/payment-type/**").hasAuthority(PrivilegeName.TIPOS_DE_PAGO.name())
                        .antMatchers("/api/school-agreement/**").hasAuthority(PrivilegeName.CONVENIOS_ESCOLARES.name())
                        .antMatchers("/api/period/**").hasAuthority(PrivilegeName.PERIODOS.name())
                        .antMatchers("/api/generation/**").hasAuthority(PrivilegeName.GENERACIONES.name())
                        .antMatchers("/api/semester/**").hasAuthority(PrivilegeName.SEMESTRES.name())
                        .antMatchers("/api/subject/**").hasAuthority(PrivilegeName.MATERIAS.name())
                        .antMatchers("/api/group/**").hasAuthority(PrivilegeName.GRUPOS.name())
                        .antMatchers("/api/schools/**").hasAuthority(PrivilegeName.ESCUELAS.name())
                        .antMatchers("/api/study-programs/**").hasAuthority(PrivilegeName.PROGRAMAS.name())
                        .antMatchers("/api/modality/**").hasAuthority(PrivilegeName.MODALIDADES.name())

                        .antMatchers("/api/lists/**").permitAll()
                        .antMatchers("/api/student/**").hasAuthority(PrivilegeName.INSCRIPCIONES.name())
                        .antMatchers("/api/inscriptions/**").permitAll()
                        .antMatchers("/api/requests/**").permitAll()
                        .antMatchers("/api/payments/**").hasAuthority(PrivilegeName.PAGOS.name())
                        .antMatchers("/api/studentrole/**").permitAll()
                        .antMatchers("/api/evaluation/**").permitAll()
                        .antMatchers("/api/refunds/**").hasAuthority(PrivilegeName.REEMBOLSOS.name())

                        .antMatchers("/api/academic-dismiss/**").permitAll()
                        .antMatchers("/api/teacher/**").hasAuthority(PrivilegeName.DOCENTES.name())
                        .antMatchers("/api/agreements/**").hasAuthority(PrivilegeName.CONTRATOS.name())
                        .antMatchers("/api/grades/**").permitAll()
                        .antMatchers("/api/schedules/**").hasAuthority(PrivilegeName.HORARIOS.name())

                        .antMatchers("/api/reports/**").hasAuthority(PrivilegeName.REPORTES.name())
                        .antMatchers("/api/documentation/**").hasAuthority(PrivilegeName.DOCUMENTOS.name())

                        .antMatchers("/api/auth/**", "/swagger-ui").permitAll()
                        .antMatchers(AUTH_WHITELIST).permitAll()
                        .antMatchers("/api/**").authenticated())
                .logout().clearAuthentication(true)
                .invalidateHttpSession(true)
                .clearAuthentication(true).deleteCookies("JSESSIONID")
                .permitAll().and().exceptionHandling()
                .authenticationEntryPoint(entryPoint)
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        logger.info("Configuramos el filtro");
        http.addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
