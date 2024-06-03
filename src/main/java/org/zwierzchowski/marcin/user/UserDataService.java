package org.zwierzchowski.marcin.user;

import org.mindrot.jbcrypt.BCrypt;
import org.zwierzchowski.marcin.exception.InvalidCredentialsFormatException;
import org.zwierzchowski.marcin.exception.InvalidPasswordException;
import org.zwierzchowski.marcin.exception.UserNotFoundException;
import org.zwierzchowski.marcin.utils.FileService;

import java.io.IOException;
import java.util.Map;

public class UserDataService {

  public User addUser(String username, String password, String role)
      throws IOException, InvalidCredentialsFormatException {
    Map<String, User> users = FileService.loadDataBase();
    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
    User newUser =
        switch (role.toLowerCase()) {
          case "user" -> new StandardUser(username, hashedPassword);
          case "admin" -> new Admin(username, hashedPassword);
          default -> throw new InvalidCredentialsFormatException("Unexpected value: " + role);
        };

    users.put(username, newUser);
    FileService.saveDataBase(users);
    return newUser;
  }

  public boolean isValidCredentials(String username, String password)
      throws IOException, UserNotFoundException, InvalidPasswordException {
    Map<String, User> users = FileService.loadDataBase();
    if (!users.containsKey(username)) {
      throw new UserNotFoundException("User not exist", username);
    }

    User user = users.get(username);
    if (!BCrypt.checkpw(password, user.getPassword())) {
      throw new InvalidPasswordException("Invalid password");
    }

    return true;
  }

  public User getUser(String username) throws IOException, UserNotFoundException {
    Map<String, User> users = FileService.loadDataBase();
    User user = users.get(username);
    if (user == null) {
      throw new UserNotFoundException("User not find:", username);
    }
    return user;
  }

  public void deleteUser(String username) throws IOException, UserNotFoundException {

    Map<String, User> users = FileService.loadDataBase();
    if (users.containsKey(username)) {
      users.remove(username);
      FileService.saveDataBase(users);
    } else {
      throw new UserNotFoundException("User not found", username);
    }
  }
}
