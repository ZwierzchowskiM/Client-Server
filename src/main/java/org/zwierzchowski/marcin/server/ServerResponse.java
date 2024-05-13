package org.zwierzchowski.marcin.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.zwierzchowski.marcin.user.User;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ServerResponse {

    private ObjectMapper mapper = new ObjectMapper();

    public String calculateUptime(Instant startTime) throws JsonProcessingException {
        Duration serverUptime = Duration.between(startTime, Instant.now());
        Map<String, Integer> uptimeResponse = new HashMap<>();
        uptimeResponse.put("hours", serverUptime.toHoursPart());
        uptimeResponse.put("minutes", serverUptime.toMinutesPart());
        uptimeResponse.put("seconds", serverUptime.toSecondsPart());
        return mapper.writeValueAsString(uptimeResponse);
    }

    public String printServerCommands(Map<String, String> commandsInfo) throws JsonProcessingException {
        return mapper.writeValueAsString(commandsInfo);
    }

    public String printServerInfo(Map<String, String> serverInfo) throws JsonProcessingException {
        return mapper.writeValueAsString(serverInfo);
    }

    public String registerUser(User user) throws JsonProcessingException {
        Map<String, User> registeredUser = new HashMap<>();
        registeredUser.put("Registered user", user);
        return mapper.writeValueAsString(registeredUser);
    }

    public String printText(String text) throws JsonProcessingException {
        Map<String, String> message = new HashMap<>();
        message.put("info", text);
        return mapper.writeValueAsString(message);
    }


    public String printError(String text) throws JsonProcessingException {
        Map<String, String> message = new HashMap<>();
        message.put("error", text);
        return mapper.writeValueAsString(message);
    }

    public String printLoginStatus(boolean status) throws JsonProcessingException {
        Map<String, String> response = new HashMap<>();
        if (status) {
            response.put("status", "success");
            response.put("message", "User sucessfully logged in");
        } else {
            response.put("status", "failure");
            response.put("message", "Incorrect username or password");
        }

        return mapper.writeValueAsString(response);
    }
}