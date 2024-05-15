package org.zwierzchowski.marcin.user;

import org.mindrot.jbcrypt.BCrypt;
import org.zwierzchowski.marcin.utils.FileService;
import java.io.IOException;
import java.util.Map;

public class UserDataService {

    public User addUser(String username, String password, String role) throws IOException {
        Map<String, User> users = FileService.loadDataBase();
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User newUser = switch (role.toLowerCase()) {
            case "user" -> new StandardUser(username, hashedPassword);
            case "admin" -> new Admin(username, hashedPassword);
            default -> throw new IllegalStateException("Unexpected value: " + role);
        };

        users.put(username,newUser);
        FileService.saveDataBase(users);
        return newUser;
    }

    public boolean isValidCredentials(String username, String password) throws IOException {
        Map<String, User> users = FileService.loadDataBase();
        if (users.containsKey(username)) {
            User user = users.get(username);
            return BCrypt.checkpw(password,user.getPassword());
        }
        return false;
    }

    public User getUser(String username) throws IOException {
        Map<String, User> users = FileService.loadDataBase();
        return users.get(username);
    }

    public boolean delete(String username) throws IOException {

        Map<String, User> users = FileService.loadDataBase();
        if (users.containsKey(username)){
            users.remove(username);
            FileService.saveDataBase(users);
            return true;
        } else {
            return false;
        }
    }
}
