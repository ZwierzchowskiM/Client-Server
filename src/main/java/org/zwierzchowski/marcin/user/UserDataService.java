package org.zwierzchowski.marcin.user;

import java.io.IOException;
import java.util.Map;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.mindrot.jbcrypt.BCrypt;
import org.zwierzchowski.marcin.db.tables.Users;
import org.zwierzchowski.marcin.exception.InvalidCredentialsFormatException;
import org.zwierzchowski.marcin.exception.InvalidPasswordException;
import org.zwierzchowski.marcin.exception.UserNotFoundException;
import org.zwierzchowski.marcin.utils.FileService;

public class UserDataService {

    private UserRepository userRepository;


    public UserDataService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDataService() {
    
  }

  public User addUser(String username, String password, String role)
      throws InvalidCredentialsFormatException {
    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
    User newUser =
        switch (role.toLowerCase()) {
          case "user" -> new StandardUser(username, hashedPassword);
          case "admin" -> new Admin(username, hashedPassword);
          default -> throw new InvalidCredentialsFormatException("Unexpected value: " + role);
        };

    userRepository.addUserToDb(username, hashedPassword, role);
    return newUser;
  }

  public boolean isValidCredentials(String username, String password)
      throws UserNotFoundException, InvalidPasswordException {

    User user = getUser(username);
    if (user == null) {
      throw new UserNotFoundException("User not exist", username);
    }

    if (!BCrypt.checkpw(password, user.getPassword())) {
      throw new InvalidPasswordException("Invalid password");
    }

    return true;
  }


  public User getUser(String username) throws UserNotFoundException {


    User user = userRepository.getUser(username);
    if (user == null) {
      throw new UserNotFoundException("User not exist", username);
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
