package mx.edu.utez.sigebe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableWebMvc
@EnableSwagger2
@SpringBootApplication
public class SigebeApplication {
    public static void main(String[] args) {
        SpringApplication.run(SigebeApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            public void addCorsMapping(CorsRegistry registry) {
                registry.addMapping("/api/*").allowedOrigins(new String[]{"*"}).allowedMethods();
            }
        };
    }
}
