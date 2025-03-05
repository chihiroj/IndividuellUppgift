package com.example.IndividuellUppgift.controller;


import com.example.IndividuellUppgift.model.MessageResponseDTO;
import com.example.IndividuellUppgift.model.User;
import com.example.IndividuellUppgift.repository.UserRepository;
import com.example.IndividuellUppgift.service.FolderService;
import com.example.IndividuellUppgift.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Containing endpoints for managing folder.
 */
@RestController
@RequiredArgsConstructor
public class FolderController {
    private final FolderService folderService;
    private final UserService userService;

    /**
     * Endpoint for  creating folder.
     * @param authorizedClient Logged in user.
     * @param folderName the name of folder to create.
     * @return message and status for action.
     */
    @PostMapping("/folder")
    public ResponseEntity<?> createFolder(@RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient authorizedClient, @RequestParam String folderName){
        String gitHubId = authorizedClient.getPrincipalName();
        Optional<User> user = userService.findByGithubId(gitHubId);
        if(user.isPresent()) {
            MessageResponseDTO mDto = new MessageResponseDTO("Success", HttpStatus.OK);
            boolean success = folderService.createFolder(folderName, user.get());
            if (success) {
                return ResponseEntity.ok(mDto);
            } else {
                return ResponseEntity.internalServerError().body(new MessageResponseDTO("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR));
            }
        }else {
            return ResponseEntity.internalServerError().body(new MessageResponseDTO("User could not be found",HttpStatus.FORBIDDEN));
        }


    }
}
