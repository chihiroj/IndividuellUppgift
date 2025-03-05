package com.example.IndividuellUppgift.service;

import com.example.IndividuellUppgift.model.MessageResponseDTO;
import com.example.IndividuellUppgift.model.User;
import com.example.IndividuellUppgift.model.UserFile;
import com.example.IndividuellUppgift.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

/**
 * Business logic for managing files.
 */
@RequiredArgsConstructor
@Service
public class FileService {

    private final FileRepository fileRepository;

    /**
     *Upload a file to a folder.
     * @param file the file to upload.
     * @param folderName the name of folder to upload to.
     * @param user the user uploading the file.
     * @return message and status for the result.
     */
    public MessageResponseDTO uploadFile(MultipartFile file, String folderName, User user){
        String userHome = System.getProperty("user.home");
        String directory = getDirectory(folderName,userHome);

        File folder = new File(directory);
        if(!folder.exists()){
            return new MessageResponseDTO("The folder does not exist.", HttpStatus.NOT_FOUND);
        }

        String originalFilename = file.getOriginalFilename();
        if(originalFilename == null || originalFilename.isEmpty()){
            return new MessageResponseDTO("Something wrong with the file. Try again. ", HttpStatus.BAD_REQUEST);
        }

        File fileToUpload = new File(folder, originalFilename);
        try{
            file.transferTo(fileToUpload);
            UserFile userFile = new UserFile();
            userFile.setFileName(folderName + File.separator + originalFilename);
            userFile.setUser(user);
            fileRepository.save(userFile);

            return new MessageResponseDTO("Success", HttpStatus.OK);
        }catch (IOException e){
            e.printStackTrace();
            return new MessageResponseDTO("Something went wrong. Try again.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     *Delete a file  from a folder.
     * @param folderName the folder containing the file to delete.
     * @param fileName the file to delete.
     * @param user the user deleting the file.
     * @return message and status for the result.
     */
    public MessageResponseDTO deleteFile(String folderName, String fileName,User user){
        Optional<UserFile> userFile = fileRepository.findByFileName(folderName + File.separator + fileName);
        if(userFile.isEmpty()) {
            return new MessageResponseDTO("File could not be found.",HttpStatus.NOT_FOUND);
        }

        if(!userFile.get().getUser().getId().equals(user.getId())){
            return new MessageResponseDTO("User is not owner of file.", HttpStatus.FORBIDDEN);
        }

        String userHome = System.getProperty("user.home");
        String directory = getDirectory(folderName, userHome);

        File folder = new File(directory);
        if(!folder.exists()){
            return new MessageResponseDTO("The folder does not exist.",HttpStatus.NOT_FOUND);
        }
        String fileLocation =  getFileLocation(folderName, fileName,userHome);
        File file = new File(fileLocation);
        if(!file.exists()){
            return new MessageResponseDTO("The file does not exist.",HttpStatus.NOT_FOUND);
        }
        boolean deleted = file.delete();
        if(deleted){
            fileRepository.delete(userFile.get());
            return new MessageResponseDTO("Success",HttpStatus.OK);
        }
        return new MessageResponseDTO("Could not delete the file.",HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Method for getting the path for a file.
     * @param folderName part of path.
     * @param fileName part of path.
     * @param userHome part of path.
     * @return returning a path.
     */
    private static String getFileLocation(String folderName, String fileName, String userHome){
        return userHome + File.separator + "Documents" + File.separator + folderName + File.separator + fileName;
    }

    /**
     * Method for getting the path for a folder.
     * @param folderName part of path.
     * @param userHome part of path.
     * @return  returning a path.
     */
    private static String getDirectory(String folderName, String userHome){
        return userHome + File.separator + "Documents" + File.separator + folderName;
    }

    /**
     *Download a file.
     * @param folderName the folder containing the file to download.
     * @param fileName the file to download.
     * @param user the user downloading a file.
     * @return message and status for the result.
     */
    public ResponseEntity<?> downloadFile(String folderName, String fileName,User  user){
        String userHome = System.getProperty("user.home");
        String directory = getDirectory(folderName,userHome);
        Optional<UserFile> userFile = fileRepository.findByFileName(folderName + File.separator + fileName);
        if(userFile.isEmpty()){
            return ResponseEntity.badRequest().body(new MessageResponseDTO("File could not be found.", HttpStatus.NOT_FOUND));
        }
        if(!userFile.get().getUser().getId().equals(user.getId())){
            return ResponseEntity.badRequest().body(new MessageResponseDTO("User is not owner of file.",HttpStatus.FORBIDDEN));
        }

        File folder = new File(directory);
        if(!folder.exists()){
            return ResponseEntity.badRequest().body(new MessageResponseDTO("The folder does not exist.",HttpStatus.NOT_FOUND));
        }

        String fileLocation = getFileLocation(folderName, fileName, userHome);
        File file = new File(fileLocation);
        if(!file.exists()){
            return ResponseEntity.badRequest().body(new MessageResponseDTO("The file does not exist.",HttpStatus.NOT_FOUND));

        }

        try{
            String contentType = Files.probeContentType(file.toPath());
            if(contentType == null){
                contentType = "application/object-stream";
            }
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, contentType);
            headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));

            return ResponseEntity.ok().headers(headers).body(resource);
        }catch (IOException e){
            return ResponseEntity.internalServerError().body(new MessageResponseDTO("Error downloading file.",HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
