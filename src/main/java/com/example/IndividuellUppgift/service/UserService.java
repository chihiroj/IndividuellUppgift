package com.example.IndividuellUppgift.service;

import com.example.IndividuellUppgift.model.User;
import com.example.IndividuellUppgift.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Business logic for managing users.
 */
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    /**
     * Find a user with githubId.
     * @param githubId The id for finding a user.
     * @return The user having the githubId in the database.
     */
    public Optional<User> findByGithubId(String githubId) {
        return userRepository.findByGithubId(githubId);
    }

}
