package com.bz.amicale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8081") // Enable CORS for your front-end application
@RestController
@RequestMapping("/users") // Base URL for the API
public class SqlController {

    @Autowired
    SqlRepository sqlRepository;  // Autowire the repository to access the database

    // Get all users, optionally filtered by name
    @GetMapping("/users")
    public ResponseEntity<List<Sql>> getAllUsers(@RequestParam(required = false) String fullName) {
        try {
            List<Sql> users = new ArrayList<>();

            // If fullName is provided, find users by name
            if (fullName == null) {
                sqlRepository.findAll().forEach(users::add);  // Get all users if no filter is provided
            } else {
                sqlRepository.findByNameContaining(fullName).forEach(users::add);  // Filter users by name
            }

            // If no users are found, return NO_CONTENT
            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(users, HttpStatus.OK);  // Return list of users
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);  // Return error if something goes wrong
        }
    }

    // Get a specific user by ID
    @GetMapping("/users/{id}")
    public ResponseEntity<Sql> getUserById(@PathVariable("id") long id) {
        Optional<Sql> userData = sqlRepository.findById(id);  // Find user by ID

        // If user found, return the user with OK status
        if (userData.isPresent()) {
            return new ResponseEntity<>(userData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // User not found
        }
    }

    // Create a new user
    @PostMapping("/users")
    public ResponseEntity<Sql> createUser(@RequestBody Sql sql) {
        try {
            // Directly save the incoming SQL object without manual construction
            Sql _sql = sqlRepository.save(sql);
            return new ResponseEntity<>(_sql, HttpStatus.CREATED);  // Return the created user with CREATED status
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);  // Error occurred
        }
    }

    // Update an existing user by ID
    @PutMapping("/users/{id}")
    public ResponseEntity<Sql> updateUser(@PathVariable("id") long id, @RequestBody Sql sql) {
        Optional<Sql> userData = sqlRepository.findById(id);  // Find user by ID

        // If user exists, update the user fields and return with OK status
        if (userData.isPresent()) {
            Sql _sql = userData.get();
            _sql.setName(sql.getName());
            _sql.setEmail(sql.getEmail());
            _sql.setMatricule(sql.getMatricule());
            _sql.setDepartement(sql.getDepartement());
            _sql.setFonction(sql.getFonction());
            return new ResponseEntity<>(sqlRepository.save(_sql), HttpStatus.OK);  // Return updated user
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // User not found
        }
    }

    // Delete a user by ID
    @DeleteMapping("/users/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") long id) {
        try {
            sqlRepository.deleteById(id);  // Delete user by ID
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // Success, no content to return
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Error occurred
        }
    }

    // Delete all users
    @DeleteMapping("/users")
    public ResponseEntity<HttpStatus> deleteAllUsers() {
        try {
            sqlRepository.deleteAll();  // Delete all users
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // Success, no content to return
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Error occurred
        }
    }
}
