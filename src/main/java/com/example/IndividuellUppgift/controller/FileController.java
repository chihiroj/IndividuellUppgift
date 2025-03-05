package com.example.IndividuellUppgift.controller;


import com.example.IndividuellUppgift.model.MessageResponseDTO;
import com.example.IndividuellUppgift.model.User;
import com.example.IndividuellUppgift.service.FileService;
import com.example.IndividuellUppgift.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * Containing endpoints for managing files.
 */
@RequiredArgsConstructor
@RestController
public class FileController {

    private final FileService fileService;
    private final UserService userService;

    /**
     * Endpoint for uploading a file.
     * @param authorizedClient Logged in user.
     * @param folderName name of the folder to upload to.
     * @param file the file to upload.
     * @return message and status for action.
     */
    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient authorizedClient, @RequestParam String folderName, @RequestParam MultipartFile file){
        String gitHuId = authorizedClient.getPrincipalName();
        Optional<User> user = userService.findByGithubId(gitHuId);
        if(user.isPresent()){
            MessageResponseDTO mDto = fileService.uploadFile(file,folderName, user.get());
            if(mDto.getHttpStatus() == HttpStatus.OK) {
                mDto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FileController.class).downloadFile(authorizedClient,folderName,file.getOriginalFilename())).withRel("download"));
                mDto.add(WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(FileController.class).deleteFile(authorizedClient, folderName, file.getOriginalFilename())
                ).withRel("delete"));
            }
            return new ResponseEntity<>(mDto, mDto.getHttpStatus());

        }

        return  ResponseEntity.internalServerError().body(new MessageResponseDTO("User could not be found", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * Endpoint for deleting a file.
     * @param authorizedClient Logged in user.
     * @param folderName name of the folder to delete from.
     * @param fileName the name of file to delete.
     * @return message and status for action.
     */
    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient authorizedClient, @RequestParam String folderName, @RequestParam String fileName ){
        String gitHubId = authorizedClient.getPrincipalName();
        Optional<User> user = userService.findByGithubId(gitHubId);

        if(user.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Could not find user",HttpStatus.FORBIDDEN));
        }
        MessageResponseDTO mDto = fileService.deleteFile(folderName,fileName,user.get());
        if(mDto.getHttpStatus() == HttpStatus.OK){
            mDto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FileController.class).uploadFile(authorizedClient,folderName, null))
                    .withRel("upload"));
        }
        return new ResponseEntity<>(mDto, mDto.getHttpStatus());
    }

    /**
     * Endpoint for downloading a file.
     * @param authorizedClient Logged in user.
     * @param folderName name of the folder to download from.
     * @param fileName the name of file to download.
     * @return message and status for action.
     */
    @GetMapping("/file")
    public ResponseEntity<?> downloadFile(@RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient authorizedClient, @RequestParam String folderName, @RequestParam String fileName){
        String gitHubId = authorizedClient.getPrincipalName();
        Optional<User> user = userService.findByGithubId(gitHubId);

        if(user.isEmpty()){
            return ResponseEntity.badRequest().body(new MessageResponseDTO("Could not find user",HttpStatus.FORBIDDEN));
        }
        return fileService.downloadFile(folderName,fileName,user.get());
    }
}
