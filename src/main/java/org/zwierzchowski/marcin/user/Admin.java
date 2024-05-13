package org.zwierzchowski.marcin.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@JsonTypeName("admin")
public class Admin extends User {
    @JsonCreator
    public Admin(@JsonProperty("username") String username,
                 @JsonProperty("password")String password) {
        super(username, password,"admin", new ArrayList<>());
    }
}
