package org.example;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class User {

    private String username;
    private String password;
    @Setter(AccessLevel.NONE)
    private String role;

}
