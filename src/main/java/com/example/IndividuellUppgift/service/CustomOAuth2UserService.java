package com.example.IndividuellUppgift.service;

import com.example.IndividuellUppgift.model.User;
import com.example.IndividuellUppgift.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * Saving logged-in user in the database.
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String githubId = oAuth2User.getAttribute("id").toString();
        String username = oAuth2User.getAttribute("login");

        User user = userRepository.findByGithubId(githubId).orElse(new User());

        user.setGithubId(githubId);
        user.setUsername(username);

        userRepository.save(user);

        return oAuth2User;
    }
}
