package org.zwierzchowski.marcin.user;

import java.sql.Connection;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.zwierzchowski.marcin.db.tables.Users;
import org.zwierzchowski.marcin.exception.InvalidCredentialsFormatException;
import org.zwierzchowski.marcin.exception.UserNotFoundException;
import org.zwierzchowski.marcin.utils.DataBaseManager;

public class UserRepository {

  DSLContext context;
  DataBaseManager dataBaseManager;

  public UserRepository() {
    dataBaseManager = new DataBaseManager();
    Connection conn = dataBaseManager.getConnection();
    context = DSL.using(conn, SQLDialect.POSTGRES);
  }

  public void saveUser(User user) {
    context
        .insertInto(Users.USERS)
        .set(Users.USERS.USERNAME, user.getUsername())
        .set(Users.USERS.PASSWORD, user.getPassword())
        .set(Users.USERS.ROLE, String.valueOf(user.getRole()))
        .execute();
  }

  public User finByUsername(String username)
      throws UserNotFoundException {

    Record userRecord =
        context.select().from(Users.USERS).where(Users.USERS.USERNAME.eq(username)).fetchOne();

    if (userRecord == null) {
      throw new UserNotFoundException("User not exist", username);
    }

    String usernameDb = userRecord.getValue(Users.USERS.USERNAME, String.class);
    String password = userRecord.getValue(Users.USERS.PASSWORD, String.class);
    String role = userRecord.getValue(Users.USERS.ROLE, String.class);
    int id = userRecord.getValue(Users.USERS.ID, Integer.class);

    User user =
        switch (role.toLowerCase()) {
          case "user" -> new StandardUser(id, usernameDb, password, null);
          case "admin" -> new Admin(id, usernameDb, password, null);
          default -> throw new IllegalArgumentException("Unexpected value: " + role);
        };
    return user;
  }

  public void deleteUser(String username) {
    context.delete(Users.USERS).where(Users.USERS.USERNAME.eq(username)).execute();
  }
}
