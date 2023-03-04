package com.example.webapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;


@SpringBootApplication
public class WebappApplication {
    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(WebappApplication.class, args);

        WebappApplication app = new WebappApplication();
    }

    public void createUser() {
        User user = new User("john@example.com", "password", "John", "Doe");
        userRepository.save(user);
    }

    public void getAllUsers() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            System.out.println(user.getUsername());
        }
    }



}
