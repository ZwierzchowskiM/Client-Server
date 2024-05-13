package org.zwierzchowski.marcin.server;

import lombok.Getter;
import lombok.Setter;
import org.zwierzchowski.marcin.user.UserDTO;

@Getter
@Setter
public class Session {

    private UserDTO user;

}
