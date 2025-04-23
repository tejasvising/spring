package com.newpackage.neetcounsel.utils;

import com.newpackage.neetcounsel.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.net.URLEncoder;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.DefaultRedirectStrategy;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    public OAuth2AuthenticationSuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication) throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();
        
        // Proper email extraction
        String email = (String) attributes.get("email");
        
        
        String token = jwtUtil.generateToken(email);
        
        if (email == null || token == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Missing authentication parameters");
            return;
        }

        // URL encode parameters
        String redirectUrl = "http://127.0.0.1:8080/dashboard" + "#" +
            "token=" + URLEncoder.encode(token, StandardCharsets.UTF_8) + "&" +
            "email=" + URLEncoder.encode(email, StandardCharsets.UTF_8);

        System.out.println("Redirecting to: " + redirectUrl); // Debug log
        redirectStrategy.sendRedirect(request, response, redirectUrl);
    }
}