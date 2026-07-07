package com.genc.hrms.config;

import com.genc.hrms.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable()) // Disabled for pure stateless REST endpoint testing
                .authorizeHttpRequests(auth -> auth
                        // 1. Explicitly allow all CORS preflight validation requests
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()

                        // 2. Allow fallback error routing mapping paths
                        .requestMatchers("/error").permitAll()

                        // Publicly expose the streamlined authentication controller route
                        .requestMatchers("/api/auth/login").permitAll()

                        // Static UI file system accessibility mappings
                        .requestMatchers(org.springframework.http.HttpMethod.GET,
                                "/",
                                "/index.html",
                                "/Frontend/**",
                                "/favicon.ico"
                        ).permitAll()

                        // Role mapping route access interceptors
                        .requestMatchers("/api/leave","/api/leave/**").hasRole("EMPLOYEE")
                        .requestMatchers("/api/payroll","/api/payroll/**").hasAnyRole("PAYROLL_OFFICER", "MANAGER")
                        .requestMatchers("/api/appraisal","/api/appraisal/**").hasRole("APPRAISAL_OFFICER")
                        .requestMatchers("/api/manager","/api/manager/**").hasRole("MANAGER")
                        .requestMatchers("/api/recruitment","/api/recruitment/**").hasRole("HR_RECRUITER")
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\": \"Logout successful\"}");
                            response.getWriter().flush();
                        })
                )
                .sessionManagement(session -> session
                        .sessionFixation(fixation -> fixation.migrateSession())
                )
                .headers(headers -> headers
                        .cacheControl(cacheControl -> cacheControl.disable())
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(CustomUserDetailsService customUserDetailsService) {
        return customUserDetailsService;
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://127.0.0.1:5500",
                "http://localhost:5500",
                "http://localhost:8080"
        ));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}