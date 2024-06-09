package ru.artosoft.vcsvpl.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    UserDetailsServiceImpl userDetailsService;
    private BCryptPasswordEncoder encoder(){ return new BCryptPasswordEncoder();}
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(encoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.
                        authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/registration", "/", "/assets/**", "css/**", "js/**").permitAll()
                        .requestMatchers("/admin").permitAll()
                        .anyRequest().authenticated())
                .formLogin((form) -> form.loginPage("/login").permitAll().defaultSuccessUrl("/welcome"))
                .logout(LogoutConfigurer::permitAll);
        return http.build();
    }
}
