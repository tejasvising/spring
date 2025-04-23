package com.newpackage.neetcounsel.config;

import org.springframework.security.config.Customizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
//import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;


import com.newpackage.neetcounsel.repository.UserRepository;
import com.newpackage.neetcounsel.service.CustomOAuth2UserService;
import com.newpackage.neetcounsel.utils.JwtUtil;
import com.newpackage.neetcounsel.utils.OAuth2AuthenticationSuccessHandler;
import com.newpackage.neetcounsel.repository.HttpCookieOAuth2AuthorizationRequestRepository;


//import com.newpackage.neetcounsel.utils.OAuth2AuthenticationSuccessHandler;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults()) 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
           //     .requestMatchers("/verify").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
            		
                    .successHandler(oauth2SuccessHandler())  .userInfoEndpoint(userInfo -> userInfo
                            .userService(customOAuth2UserService())  // Changed to use custom service
                            ).authorizationEndpoint(authorization -> 
                            authorization.authorizationRequestRepository(cookieAuthorizationRequestRepository())))
            
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
            .logout(logout -> logout
                    .logoutUrl("/api/auth/logout")  // Unified logout endpoint
                    .logoutSuccessHandler(logoutSuccessHandler())
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .clearAuthentication(true)
                )
            .addFilterBefore(new JwtAuthFilter(jwtUtil, userRepository), 
                    UsernamePasswordAuthenticationFilter.class) 
            .build();
    }
    @Bean
    public CustomOAuth2UserService customOAuth2UserService() {
        return new CustomOAuth2UserService(userRepository);
    }
    @Bean
    public OAuth2AuthenticationSuccessHandler oauth2SuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler(jwtUtil);
    }
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"message\":\"Logout successful\",\"status\":200}"
            );
        };
    }
    
    private AuthorizationRequestRepository<OAuth2AuthorizationRequest> cookieAuthorizationRequestRepository() {
		return new HttpCookieOAuth2AuthorizationRequestRepository();
	}
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

