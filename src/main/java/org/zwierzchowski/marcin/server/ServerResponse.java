package org.zwierzchowski.marcin.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.zwierzchowski.marcin.message.Message;

public class ServerResponse {

  private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  public String calculateUptime(Instant startTime) throws JsonProcessingException {
    Duration serverUptime = Duration.between(startTime, Instant.now());
    Map<String, Integer> uptimeResponse = new HashMap<>();
    uptimeResponse.put("hours", serverUptime.toHoursPart());
    uptimeResponse.put("minutes", serverUptime.toMinutesPart());
    uptimeResponse.put("seconds", serverUptime.toSecondsPart());
    return mapper.writeValueAsString(uptimeResponse);
  }

  public String printServerCommands(Map<String, String> commandsInfo)
      throws JsonProcessingException {
    return mapper.writeValueAsString(commandsInfo);
  }

  public String printServerInfo(Map<String, String> serverInfo) throws JsonProcessingException {
    return mapper.writeValueAsString(serverInfo);
  }

  public String printText(String text) throws JsonProcessingException {
    return mapper.writeValueAsString(text);
  }

  public String printError(String text) throws JsonProcessingException {
    Map<String, String> message = new HashMap<>();
    message.put("error", text);
    return mapper.writeValueAsString(message);
  }

  public String printLoginStatus(boolean status) throws JsonProcessingException {
    Map<String, String> response = new HashMap<>();
    if (status) {
      response.put("status", "User sucessfully logged in");
    } else {
      response.put("status", "Incorrect username or password");
    }
    return mapper.writeValueAsString(response);
  }

  public String printUnreadMessages(List<Message> unreadMessages) throws JsonProcessingException {
    Map<String, Message> messagesResponse = new LinkedHashMap<>();
    for (int i = 0; i < unreadMessages.size(); i++) {
      Message m = unreadMessages.get(i);
      messagesResponse.put("Message " + (i + 1), m);
    }
    return mapper.writeValueAsString(messagesResponse);
  }
}
