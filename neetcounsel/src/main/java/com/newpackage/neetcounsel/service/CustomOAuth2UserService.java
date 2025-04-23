package com.newpackage.neetcounsel.service;





	import com.newpackage.neetcounsel.models.User;
	import com.newpackage.neetcounsel.repository.UserRepository;
	import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
	import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
	import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
	import org.springframework.security.oauth2.core.user.OAuth2User;
	import org.springframework.stereotype.Service;

	import java.util.Map;

	@Service
	public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	    private final UserRepository userRepository;

	    public CustomOAuth2UserService(UserRepository userRepository) {
	        this.userRepository = userRepository;
	    }

	    @Override
	    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
	        OAuth2User oauth2User = super.loadUser(userRequest);
	        Map<String, Object> attributes = oauth2User.getAttributes();

	        String email = (String) attributes.get("email");
	        if (email == null) {
	            throw new OAuth2AuthenticationException("Email not found in OAuth2 response");
	        }
	        String name = (String) attributes.get("name");
	        String providerId = (String) attributes.get("sub");

	        userRepository.findByEmail(email).orElseGet(() -> {
	            User newUser = new User();
	            newUser.setEmail(email);
	            newUser.setName(name);
	            newUser.setAuthProvider("google");
	            newUser.setProviderId(providerId);
	            return userRepository.save(newUser);
	        });

	        return oauth2User;
	    }
	}

