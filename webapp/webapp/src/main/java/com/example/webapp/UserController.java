package com.example.webapp;


import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;


import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

/*    @Autowired
    private Authorization authorizeService;*/


    Map<String, Object> m = new HashMap<String, Object>();

    public String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }

    @GetMapping("/")
    public String index() {
        return ("Hello CSYE6255!" + getTime());
    }

    /*@GetMapping(path = "/user/{userId}", produces = "application/jason")
    public ResponseEntity<Object> getUserInfo(@RequestHeader HttpHeaders headers, @PathVariable String userId) throws JSONException, Exception {
        if (!userService.checkIfUserExists(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new JSONObject().put("Message", "User does not exist").toString());
        }
        JSONObject userObject = userService.getUser(userId);
        String userName = userObject.getString("email");
        String firstName = userObject.getString("first name");
        String lastName = userObject.getString("last name");
        JSONObject item = new JSONObject();
        item.put("user ID", userName);
        item.put("first name", firstName);
        item.put("last name", lastName);
        item.put("time", getTime());
        JSONArray json = new JSONArray();
        json.put(item);

        return ResponseEntity.ok().body(json.toString());
    }*/

    @PostMapping(path = "/user", produces = "application/json")
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> userMap) {
        String emailAddress = userMap.get("Email Address");
        String password = userMap.get("Password");
        String firstName = userMap.get("First Name");
        String lastName = userMap.get("Last Name");

        // Check if user with same email address already exists
        if (userService.getUserByEmail(emailAddress) != null) {
            return ResponseEntity.badRequest().body("User already exists");
        }

        User user = new User(emailAddress, password, firstName, lastName);
        System.out.println(user.getId());
        User createdUser = userService.createUser(user);
        System.out.println(password);
        System.out.println(createdUser);
        System.out.println(createdUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);

    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserById(@PathVariable Long id, @RequestBody Map<String, String> userMap, Principal principal) {

        String emailAddress = userMap.get("Email Address");
        String firstName = userMap.get("First Name");
        String lastName = userMap.get("Last Name");

        String userEmail = principal.getName();
        User userActing = userService.getUserByEmail(userEmail);

        // Get user by id
        User userShouldBe = userService.getUserById(id);

        // Check if user who created the product is making the request
        if (!userActing.equals(userShouldBe)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // Check if the email address already exists in the database
        Optional<User> existingUser = userRepository.findByEmail(emailAddress);
        if (!existingUser.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        User user = existingUser.get();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAccountUpdated(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);

    }


    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id, Principal principal) {
        System.out.println("id: " + id);
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        String userEmail = principal.getName();
        User userActing = userService.getUserByEmail(userEmail);

        // Check if user who created the product is making the request
        if (!userActing.equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Map<String, String> userData = new HashMap<>();
        userData.put("id", user.getId().toString());
        userData.put("emailAddress", user.getUsername());
        userData.put("firstName", user.getFirstName());
        userData.put("lastName", user.getLastName());

        Logger.getLogger(getClass().getName()).log(Level.INFO, "User retrieved: " + user);
        System.out.println(user);
        return ResponseEntity.ok(user);
    }


    @DeleteMapping(path = "{id}", produces = "application/json")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Principal principal) {

        String userEmail = principal.getName();
        User userActing = userService.getUserByEmail(userEmail);

        // Get user by id
        User user = userService.getUserById(id);

        // Check if user who created the product is making the request
        if (!userActing.equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }


        // Delete product and return success response
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}