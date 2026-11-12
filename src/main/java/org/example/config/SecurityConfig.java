package org.example.config;

import lombok.RequiredArgsConstructor;
import org.example.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Permite usar anotações como @PreAuthorize("hasRole('ADMIN')") nos Controllers
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita proteção CSRF pois usamos tokens JWT (Stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // Configura as permissões de acesso por rota
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/**",      // Libera rotas de Login e Cadastro
                                "/v3/api-docs/**",      // Libera JSON da documentação Swagger
                                "/swagger-ui/**",       // Libera interface visual do Swagger
                                "/swagger-ui.html"
                        ).permitAll()
                        .anyRequest().authenticated()   // Tranca todas as outras rotas do CRM
                )

                // Define que a API não guarda estado de sessão (Stateless)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Vincula o nosso provedor de banco de dados e coloca o filtro JWT antes do filtro padrão
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
