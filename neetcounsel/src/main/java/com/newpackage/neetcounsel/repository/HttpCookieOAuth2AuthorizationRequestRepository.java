package com.newpackage.neetcounsel.repository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import com.newpackage.neetcounsel.utils.CookieUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class HttpCookieOAuth2AuthorizationRequestRepository 
    implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    private static final int COOKIE_EXPIRE_SECONDS = 7*24*60*60;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return CookieUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, 
                                          HttpServletRequest request,
                                          HttpServletResponse response) {
        if (authorizationRequest == null) {
            CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
            return;
        }
        System.out.println("i am in saveAuthorizationRequest");
        CookieUtils.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                CookieUtils.serialize(authorizationRequest), COOKIE_EXPIRE_SECONDS);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
    	System.out.println("i reached removeAuthorizationRequest");
        OAuth2AuthorizationRequest authorizationRequest = loadAuthorizationRequest(request);
        System.out.println("got  authorizationRequest in removeAuthorizationRequest");
        CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        System.out.println("deleted cookie here");
        return authorizationRequest;
    }
}
