package pl.urban.taw_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.urban.taw_backend.security.JwtTokenFilter;
import pl.urban.taw_backend.service.GoogleAuthService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;
    private final GoogleAuthService googleAuthService;

    public SecurityConfig(JwtTokenFilter jwtTokenFilter, GoogleAuthService googleAuthService) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.googleAuthService = googleAuthService;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests( auth -> {
                    auth.requestMatchers("/api/auth/").authenticated();
                    auth.requestMatchers("/api/address/").authenticated();
                    auth.requestMatchers("/api/menu/add", "/api/menu/update", "/api/menu/delete").hasRole("admin");
                    auth.anyRequest().permitAll();
                    }
                )
                .sessionManagement( session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2Login(oauth2login -> {
                    oauth2login
                            .successHandler((request, response, authentication) ->{
                                OAuth2User principal = (OAuth2User) authentication.getPrincipal();
                                String token = googleAuthService.googleLogin(principal);
                                response.sendRedirect("http://localhost:4200?token=" + token);
                            })
                            .failureHandler((request, response, exception) -> {
                                response.sendRedirect("http://localhost:4200?error=true");
                            });
                })
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}
