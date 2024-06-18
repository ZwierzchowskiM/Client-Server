package org.zwierzchowski.marcin.user;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.mindrot.jbcrypt.BCrypt;
import org.zwierzchowski.marcin.db.tables.Users;
import org.zwierzchowski.marcin.db.tables.records.UsersRecord;
import org.zwierzchowski.marcin.exception.InvalidCredentialsFormatException;
import org.zwierzchowski.marcin.exception.InvalidPasswordException;
import org.zwierzchowski.marcin.exception.UserNotFoundException;
import org.zwierzchowski.marcin.utils.DataBaseManager;
import org.zwierzchowski.marcin.utils.FileService;

public class UserDataService {

  DSLContext context;
  DataBaseManager dataBaseManager;

  public UserDataService() {
    dataBaseManager = new DataBaseManager();
    Connection conn = dataBaseManager.getConnection();
    context = DSL.using(conn, SQLDialect.POSTGRES);
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

    UsersRecord usersRecord = context.newRecord(Users.USERS);
    usersRecord.setUsername(username);
    usersRecord.setPassword(password);
    usersRecord.setRole(role.toLowerCase());
    usersRecord.store();

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

  public void getAllUsers() {

    Result<Record> users = context.select().from(Users.USERS).fetch();

    users.forEach(
        user -> {
          Integer id = user.getValue(Users.USERS.ID);
          String username = user.getValue(Users.USERS.USERNAME);
          String role = user.getValue(Users.USERS.ROLE);

          System.out.printf("User %s  has id: %d and role: %s", username, id, role);
        });
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
