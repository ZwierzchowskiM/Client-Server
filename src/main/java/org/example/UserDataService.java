package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserDataService {

    private final ObjectMapper mapper = new ObjectMapper();
    private static final String FILE_PATH = "./users.json";

    public void addUser(User newUser) throws IOException {
        List<User> users = loadUsers();
        users.add(newUser);
        saveUsers(users);
    }

    public List<User> loadUsers() throws IOException {
        File file = new File(FILE_PATH);
        if (file.exists() && file.length() != 0) {
            return mapper.readValue(file, new TypeReference<List<User>>() {});
        } else {
            return new ArrayList<>();
        }
    }

    public void saveUsers(List<User> users) throws IOException {
        File file = new File(FILE_PATH);
        mapper.writerFor(new TypeReference<List<User>>() { }).writeValue(file, users);
    }
}
