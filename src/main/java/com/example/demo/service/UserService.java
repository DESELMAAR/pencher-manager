package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * LAYER 3: SERVICE (Business Logic)
 * Contains the "business rules". Controller delegates here; Service uses Repository.
 * Good place for validation, calculations, and orchestration.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get all users from the database.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get one user by id (returns Optional in case user not found).
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Create and save a new user.
     */
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }
}
