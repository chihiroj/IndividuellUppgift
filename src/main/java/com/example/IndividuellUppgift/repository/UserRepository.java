package com.example.IndividuellUppgift.repository;

import com.example.IndividuellUppgift.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository class for communicating with user table in the database.
 */
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByGithubId(String githubId);
}
