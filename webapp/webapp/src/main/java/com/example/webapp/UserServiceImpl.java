package com.example.webapp;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getUsername());

        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("User with email " + user.getUsername() + " already exists.");
        }

        LocalDateTime now = LocalDateTime.now();
        user.setAccountCreated(now);
        user.setAccountUpdated(now);
        String encodedPassword = "{bcrypt}" + passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // Save the user
        return userRepository.save(user);
    }



    @Override
    public User updateUser(Long userId, User user) {
        return null;
    }

    @Override
    public User updateUser(String email, String firstName, String lastName, String password) {
        // Find the user in the database by email
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User with email " + email + " not found");
        }
        User user = userOptional.get();

        // Update the user's first name and last name
        if (firstName != null) {
            user.setFirstName(firstName);
        }
        if (lastName != null) {
            user.setLastName(lastName);
        }

        // If the user has provided a new password, update it
        if (password != null) {
            String encodedPassword = "{bcrypt}" + passwordEncoder.encode(password);
            user.setPassword(encodedPassword);
        }

        // Update the account_updated field
        user.setAccountUpdated(LocalDateTime.now());

        // Save the updated user in the database
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User with ID " + userId + " not found");
        }
        return userOptional.get();
    }

    @Override
    public User getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            return null;
        }
        return userOptional.get();
    }


}


