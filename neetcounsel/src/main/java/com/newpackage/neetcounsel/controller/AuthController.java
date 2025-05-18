package com.newpackage.neetcounsel.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
import com.newpackage.neetcounsel.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.newpackage.neetcounsel.repository.UserRepository;
import com.newpackage.neetcounsel.utils.CookieUtils;
import com.newpackage.neetcounsel.utils.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationOauth2;
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request, HttpServletResponse response) {
        String email = request.get("email");
        String password = request.get("password");
        String name = request.get("name");

        if (email == null || password == null || name == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Email, password, and name are required"
            ));
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Email already in use"
            ));
        }

        // Create new user
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // hash password
        user.setName(name);
        user.setAuthProvider("local");

        userRepository.save(user);

        // Generate JWT
        String token = jwtUtil.generateToken(email);

        // Set Secure Cookie
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .domain("localhost")
                .sameSite("Lax")
                .build();

        response.setHeader("Set-Cookie", cookie.toString());
        
        // Return minimal user info
        return ResponseEntity.ok(Map.of(
            "userid", user.getId(),
            "message", "Registration successful"
        ));
    }


 
  //  @CrossOrigin(origins ="http://localhost:3000", allowedHeaders = "*", allowCredentials = "true", methods = {RequestMethod.POST} )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request, HttpServletResponse response) {
    	System.out.println("in /api/auth/login");
        String email = request.get("email");
        String password = request.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Email and password must be provided"
            ));
        }

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "error", "Invalid email or password"
            ));
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "error", "Invalid email or password"
            ));
        }

        String token = jwtUtil.generateToken(email);
      //  session.setAttribute("token", token);
        System.out.println("tokenme:"+token);
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                //.domain("localhost")
                .sameSite("null")
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        
        /*CookieUtils.addCookie(response, "token",
        		token, 7*24*60*60);*/
        return ResponseEntity.ok(Map.of(
            "userid", user.getId(),
            "token", token
        ));
    }

    
    @GetMapping("/oauth-success")
    public ResponseEntity<?> oauthSuccess(@RequestParam String token) {
        return ResponseEntity.ok(Map.of("token", token));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
    	System.out.println("logout is called");
        // Clear Spring Security Context
    	//session.invalidate();
        SecurityContextHolder.clearContext();
        new SecurityContextLogoutHandler().logout(request, response, null);
       // cookieAuthorizationOauth2.removeAuthorizationRequest(request,response);
        // 🛡️ Delete the token cookie by setting it empty with maxAge=0
        ResponseCookie deleteCookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(request.isSecure())
                .path("/")
                .domain("localhost")
                .maxAge(0) // expire immediately
                .sameSite("Lax")
                .build();
        

        response.setHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
        response.setHeader("Cache-Control", "no-store");

        return ResponseEntity.ok(Map.of(
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
