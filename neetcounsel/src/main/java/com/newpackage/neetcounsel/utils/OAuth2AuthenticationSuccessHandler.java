package com.newpackage.neetcounsel.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import com.newpackage.neetcounsel.models.User;
import com.newpackage.neetcounsel.repository.UserRepository;
import jakarta.servlet.http.Cookie;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    public OAuth2AuthenticationSuccessHandler(JwtUtil jwtUtil,UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository=userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Authentication authentication) throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();
        
        String email = (String) attributes.get("email");
        
        if (email == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Missing email");
            return;
        }

        String token = jwtUtil.generateToken(email);
        Optional<User> user=userRepository.findByEmail(email);
        
        ResponseCookie cookie = ResponseCookie.from("token", token)
        	    .httpOnly(true)
        	    .secure(false)
        	    .path("/")
        	    .maxAge(7*24 * 60 * 60)
        	    .sameSite("Strict")
        	    .build();

        	response.setHeader("Set-Cookie", cookie.toString());
        // Redirect to frontend with token and email as query parameters
        String redirectUrl = "http://localhost:3000/select"+"?userID="+user.get().getId();
        System.out.println("Redirecting to: " + redirectUrl);
        
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
