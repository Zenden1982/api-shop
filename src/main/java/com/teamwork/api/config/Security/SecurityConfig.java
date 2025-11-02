package com.teamwork.api.config.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.teamwork.api.service.UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final UserService userService;

        private final JwtRequestFilter jwtRequestFilter;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .cors(withDefaults())
                                .headers(headers -> headers.frameOptions(
                                                frameOptionsCustomizer -> frameOptionsCustomizer.disable()))
                                .authorizeHttpRequests(requests -> requests.requestMatchers(
                                                "/", "/static/assets/**", "/assets/**", "/test", "/index.html",
                                                "/favicon.ico",
                                                "/css/**", "/js/**", "/images/**", "/static/**",
                                                "/webjars/**", "/swagger-ui/**", "/v3/api-docs/**",
                                                "/swagger-ui.html", "/h2-console/**").permitAll()
                                                .requestMatchers(HttpMethod.POST,
                                                                "/api/v1/users", // Регистрация
                                                                "/api/v1/users/login", // Вход
                                                                "/api/v1/auth/refresh",
                                                                "/api/v1/webhooks/**")
                                                .permitAll()

                                                // Разрешаем все GET-запросы к API
                                                .requestMatchers(HttpMethod.GET, "/api/**").permitAll()

                                                // Все остальные запросы (например, POST, PUT, DELETE к защищенным
                                                // ресурсам)
                                                // требуют роли ADMIN
                                                .anyRequest().hasRole("ADMIN"))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(
                                                                org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(
                                                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
                        throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userService);
                authProvider.setPasswordEncoder(passwordEncoder);
                return authProvider;
        }
}