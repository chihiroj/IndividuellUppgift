package com.example.IndividuellUppgift.service;

import com.example.IndividuellUppgift.model.User;
import com.example.IndividuellUppgift.model.UserFile;
import com.example.IndividuellUppgift.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Business logic for managing folders.
 */
@Service
@RequiredArgsConstructor
public class FolderService {
    private final FileRepository fileRepository;

    /**
     *Creates a new folder in the user's Documents directory and save in the database.
     * @param folderName the name of folder to create.
     * @param user the user creating the folder.
     * @return return true if success or false if error.
     */
    public boolean createFolder(String folderName, User user){
        String directory = System.getProperty("user.home") + File.separator + "Documents" + File.separator + folderName;
        File f = new File(directory);
        UserFile userFile = new UserFile();
        userFile.setFileName(folderName);
        userFile.setUser(user);
        fileRepository.save(userFile);

        return f.mkdirs();
    }

}
