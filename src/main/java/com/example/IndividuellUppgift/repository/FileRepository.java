package com.example.IndividuellUppgift.repository;

import com.example.IndividuellUppgift.model.UserFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository class for communicating with file table in database.
 */
@Repository
public interface FileRepository extends JpaRepository <UserFile,Long>{
    Optional<UserFile> findByFileName(String fileName);
}
