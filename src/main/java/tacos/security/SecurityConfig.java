package tacos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
         http.authorizeHttpRequests((authorizeRequests) ->
                authorizeRequests
                        .requestMatchers(mvc.pattern("/design"), mvc.pattern("/orders"))
                            .hasRole("USER")
                        .requestMatchers(mvc.pattern("/"), mvc.pattern("/**"))
                            .permitAll())
        .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/design", false))
                .oauth2Login(Customizer.withDefaults())
                .logout(Customizer.withDefaults());
         // TODO to continue with API registration (to use OAuth from google), I need to register my app. I need a domain/application home page. So,
        // TODO register domain name first (buy a domain address) first, and deploy app there.
//                 .oauth2Client(Customizer.withDefaults()); // TODO find out why I cant have both in the application.yml
        // TODO find out why the user is sent away (403) when trying to submit their taco order.
        return http.build();
    }
}
