package org.zwierzchowski.marcin.user;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.zwierzchowski.marcin.db.tables.Users;
import org.zwierzchowski.marcin.db.tables.records.UsersRecord;
import org.zwierzchowski.marcin.exception.UserNotFoundException;
import org.zwierzchowski.marcin.utils.DataBaseManager;
import org.jooq.Record;

import java.sql.Connection;

public class UserRepository {

    DSLContext context;
    DataBaseManager dataBaseManager;

    public UserRepository() {
        dataBaseManager = new DataBaseManager();
        Connection conn = dataBaseManager.getConnection();
        context = DSL.using(conn, SQLDialect.POSTGRES);
    }

    public void addUserToDb (String username, String password, String role) {
        UsersRecord usersRecord = context.newRecord(Users.USERS);
        usersRecord.setUsername(username);
        usersRecord.setPassword(password);
        usersRecord.setRole(role.toLowerCase());
        usersRecord.store();
    }

    public User getUser(String username) throws UserNotFoundException {

        Record record =
            context.select().from(Users.USERS).where(Users.USERS.USERNAME.eq(username)).fetchOne();

        if (record == null) {
            throw new UserNotFoundException("User not exist", username);
        }

        String usernameDb = record.get(DSL.field("username", String.class));
        String password = record.get(DSL.field("password", String.class));
        String role = record.get(DSL.field("role", String.class));

        User user =
                switch (role) {
                    case "user" -> new StandardUser(usernameDb, password);
                    case "admin" -> new Admin(usernameDb, password);
                    default -> throw new UserNotFoundException("Unexpected value: ", username);
                };
        return user;
    }
}
