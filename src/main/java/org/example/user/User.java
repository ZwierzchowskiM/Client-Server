package org.example.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.message.Message;
import org.example.user.Admin;
import org.example.user.StandardUser;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Admin.class, name = "admin"),
        @JsonSubTypes.Type(value = StandardUser.class, name = "standard")})
@Getter
@Setter
@AllArgsConstructor
public abstract class User {

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @Setter(AccessLevel.NONE)
    @JsonProperty("role")
    private String role;

    @JsonProperty("messages")
    private List<Message> messages;

    public boolean inboxIsFull() {
       return messages.size()<5;
    }
}
