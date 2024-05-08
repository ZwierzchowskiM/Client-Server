package org.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeName("normal")
public class StandardUser extends User {

    @JsonCreator
    public StandardUser(@JsonProperty("username") String username,
                        @JsonProperty("password")String password) {
        super(username, password, "standard");
    }

}

