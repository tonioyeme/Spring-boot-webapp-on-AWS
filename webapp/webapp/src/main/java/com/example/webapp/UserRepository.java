package com.example.webapp;

import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}



/*@Repository
public class UserRepository {
    Map<String, JSONObject> userInfoMap = new HashMap<>();

    public boolean checkIfUserExist(String userId) {
        return userInfoMap.containsKey(userId);
    }

    public void addUser(String userId, JSONObject userObject) {

        userInfoMap.put(userId, userObject);
    }

    public JSONObject getUser(String userId) {
        if (checkIfUserExist(userId)) {
            return (userInfoMap.get(userId));
        }
        return null;
    }

    public void deleteUser(String userId) {
        userInfoMap.remove(userId);
    }

    public void updateUserInfo(String userId, JSONObject userObject) {
        userInfoMap.put(userId, userObject);

    }
}*/

