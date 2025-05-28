package com.newpackage.neetcounsel.config;

import org.springframework.security.config.Customizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
//import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.web.cors.CorsConfigurationSource;

import com.newpackage.neetcounsel.repository.UserRepository;
import com.newpackage.neetcounsel.service.CustomOAuth2UserService;
import com.newpackage.neetcounsel.utils.CookieUtils;
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
//    @Autowired private CorsConfig corsConfig;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/oauth2/**").permitAll()
               // .requestMatchers("/get-key").permitAll()
                .anyRequest().authenticated()
            )
            /*.formLogin(form -> form
                    .loginProcessingUrl("/api/auth/login")  // Processing URL
                    .usernameParameter("email")            // Match your form field
                    .passwordParameter("password")
                    .successHandler(authenticationSuccessHandler())
                    .failureHandler(authenticationFailureHandler())
                    .permitAll()
                )*/
            .oauth2Login(oauth2 -> oauth2
            		
                    .successHandler(oauth2SuccessHandler())  .userInfoEndpoint(userInfo -> userInfo
                            .userService(customOAuth2UserService())  // Changed to use custom service
                            ).authorizationEndpoint(authorization -> 
                            authorization.authorizationRequestRepository(cookieAuthorizationRequestRepository())))
           
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ) 
            .logout(logout -> logout
                   
                    .logoutSuccessHandler(logoutSuccessHandler())
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .clearAuthentication(true)
                    //.cookieAuthorizationRequestRepository.removeAuthorizationRequest()
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
        return new OAuth2AuthenticationSuccessHandler(jwtUtil,userRepository);
    }
    
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String token = jwtUtil.generateToken(authentication.getName());
            
           /* ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(86400)
                .build();
            
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());*/
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":200}");
        };
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Login failed\"}");
        };
    }
    
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
        	ResponseCookie cookie = ResponseCookie.from("oauth2_auth_request", "")
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(0)
                    .build();
                
                response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
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

