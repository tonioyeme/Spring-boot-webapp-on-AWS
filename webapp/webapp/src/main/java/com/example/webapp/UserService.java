package com.example.webapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

public interface UserService {
    User createUser(User user);

    User updateUser(Long userId, User user);

    User updateUser(String email, String firstName, String lastName, String password);

    User getUserById(Long userId);

    User getUserByEmail(String email);

}


/*
@Repository
public class UserService {

    @Autowired
    UserRepository userRepository;

    public void newUserRegister(String userId, JSONObject userObject){
        userRepository.addUser(userId, userObject);
    }

    public boolean checkIfUserExists(String userId){
        return userRepository.checkIfUserExist(userId);
    }

    public JSONObject getUser(String userId) {
        return userRepository.getUser(userId);
    }

    public void deleteUser(String userId) {
        userRepository.deleteUser(userId);
    }

    public void updateUserInfo(String userId, JSONObject userObject) {
        userRepository.updateUserInfo(userId, userObject);
    }

}
*/
