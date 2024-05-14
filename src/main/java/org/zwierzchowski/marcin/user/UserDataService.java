package org.zwierzchowski.marcin.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserDataService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String FILE_PATH = "./users.json";

    public User addUser(String username, String password, String role) throws IOException {
        Map<String, User> users = loadUsers();
        User newUser = switch (role) {
            case "standard" -> new StandardUser(username, password);
            case "admin" -> new Admin(username, password);
            default -> throw new IllegalStateException("Unexpected value: " + role);
        };

        users.put(username,newUser);
        saveUsers(users);
        return newUser;
    }

    public Map<String,User> loadUsers() throws IOException {
        File file = new File(FILE_PATH);
        if (file.exists() && file.length() != 0) {
            return OBJECT_MAPPER.readValue(file, new TypeReference<Map<String,User>>() {});
        } else {
            return new HashMap<>();
        }
    }

    public void saveUsers(Map<String,User> users) throws IOException {
        File file = new File(FILE_PATH);
        OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        OBJECT_MAPPER.writerFor(new TypeReference<Map<String,User>>() { }).writeValue(file, users);
    }

    public boolean isValidCredentials(String username, String password) throws IOException {
        Map<String, User> users = loadUsers();
        if (users.containsKey(username)) {
            User user = users.get(username);
            return password.equals(user.getPassword());
        }
        return false;
    }

    public User getUser(String username) throws IOException {
        Map<String, User> users = loadUsers();
        return users.get(username);
    }

    public boolean delete(String username) throws IOException {

        Map<String, User> users = loadUsers();
        if (users.containsKey(username)){
            users.remove(username);
            saveUsers(users);
            return true;
        } else {
            return false;
        }
    }
}
