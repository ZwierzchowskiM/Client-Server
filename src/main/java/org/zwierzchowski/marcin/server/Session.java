package org.zwierzchowski.marcin.server;

import lombok.Getter;
import lombok.Setter;
import org.zwierzchowski.marcin.user.UserDTO;

@Getter
@Setter
public class Session {

    private UserDTO loggedInUser;

    public boolean isUserLoggedIn() {
        return loggedInUser != null;
    }

    public boolean isAdminLoggedIn() {
        return loggedInUser != null && loggedInUser.role().equals("ADMIN");
    }

    public void logoutUser() {
        loggedInUser = null;
    }
}
