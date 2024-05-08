package org.example.server;

import lombok.Getter;
import lombok.Setter;
import org.example.user.UserDTO;

@Getter
@Setter
public class Session {

    private UserDTO user;

}
