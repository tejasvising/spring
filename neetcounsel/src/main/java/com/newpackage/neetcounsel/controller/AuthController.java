package com.newpackage.neetcounsel.controller;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.newpackage.neetcounsel.models.User;
import com.newpackage.neetcounsel.repository.UserRepository;
import com.newpackage.neetcounsel.utils.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String name = request.get("name");

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setAuthProvider("local");

        userRepository.save(user);
        String token = jwtUtil.generateToken(email);
        return ResponseEntity.ok(Map.of("userid", user.getId(),"token",token));
    }

    @PostMapping("/login")
  //  @CrossOrigin(origins ="http://localhost:3000", allowedHeaders = "*", allowCredentials = "true", methods = {RequestMethod.POST} )
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
    	try {
        String email = request.get("email");
        String password = request.get("password");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(email);
        return ResponseEntity.ok(Map.of("userid", user.getId(),"token",token));
    	}
    	catch(Exception e){
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");  	}
    }
    
    @GetMapping("/oauth-success")
    public ResponseEntity<?> oauthSuccess(@RequestParam String token) {
        return ResponseEntity.ok(Map.of("token", token));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, 
                                  HttpServletResponse response) {
        // Clear any authentication
        SecurityContextHolder.clearContext();
        
        // For JWT tokens - client should delete the token
        // For OAuth2 - we'll invalidate the session
        new SecurityContextLogoutHandler().logout(request, response, null);
        
        return ResponseEntity.ok().body(Map.of(
            "message", "Logout successful",
            "timestamp", Instant.now().toString()
        ));
    }
   /* 
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOrigins("http://localhost:3000")
                    .allowedMethods("*");
            }
        };
    }
   */
    
    
}
