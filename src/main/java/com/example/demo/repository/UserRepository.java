package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * LAYER 2: REPOSITORY (Data Access)
 * Talks to the database. You don't write SQL — Spring Data JPA generates it.
 * JpaRepository gives you: findAll(), findById(), save(), deleteById(), etc.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring generates the query from the method name
    List<User> findByNameContainingIgnoreCase(String name);
}
