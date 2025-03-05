package com.example.IndividuellUppgift.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Set up authentication for protecting endpoints and oauth2 login.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/file").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/file").authenticated()
                        .requestMatchers(HttpMethod.GET, "/file").authenticated()
                        .requestMatchers(HttpMethod.POST, "/folder").authenticated()
                        .anyRequest().permitAll())
                .oauth2Login(oauth -> {
                    oauth.successHandler((((request, response, authentication) -> {
                        System.out.println("Success.");
                        OAuth2User user = (OAuth2User) authentication.getPrincipal();
                        user.getAttributes().forEach((key,value)->{
                            System.out.println(key + ": " + value);
                        });

                        response.sendRedirect("/welcome.html");
                    })));
                });
        return http.build();
    }
}
