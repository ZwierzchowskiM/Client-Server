package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserDataService {

    private final ObjectMapper mapper = new ObjectMapper();
    private static final String FILE_PATH = "./users.json";

    public void addUser(User newUser) throws IOException {
        Map<String, User> users = loadUsers();
        users.put(newUser.getUsername(),newUser);
        saveUsers(users);
    }

    public Map<String,User> loadUsers() throws IOException {
        File file = new File(FILE_PATH);
        if (file.exists() && file.length() != 0) {
            return mapper.readValue(file, new TypeReference<Map<String,User>>() {});
        } else {
            return new HashMap<String,User>();
        }
    }

    public void saveUsers(Map<String,User> users) throws IOException {
        File file = new File(FILE_PATH);
        mapper.writerFor(new TypeReference<Map<String,User>>() { }).writeValue(file, users);
    }
}
