package org.zwierzchowski.marcin.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@JsonTypeName("standard")
public class StandardUser extends User {

    @JsonCreator
    public StandardUser(@JsonProperty("username") String username,
                        @JsonProperty("password")String password) {
        super(username, password, Role.USER, new ArrayList<>());
    }

}

