package org.zwierzchowski.marcin.server;

import lombok.Getter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ServerData {

  private final Map<String, String> commandsInfo = new HashMap<>();
  private final Map<String, String> severInfo = new HashMap<>();
  private static final String SERVER_VERSION = "0.3.0";
  private static final String SERVER_CREATION_DATE = "19.04.2024";
  private Instant startTime;

  public ServerData() {
    initialize();
  }

  private void initialize() {
    commandsInfo.put("uptime", "Returns the server's uptime");
    commandsInfo.put("info", "Returns the server's version number and creation date");
    commandsInfo.put("help", "Returns a list of available commands with a brief description");
    commandsInfo.put("stop", "stops both the server and the client simultaneously");
    commandsInfo.put("register", "Add new user");
    commandsInfo.put("login", "Login existing user");
    commandsInfo.put("logout", "Logout user");
    commandsInfo.put("delete", "Delete existing user. Required admin role");
    commandsInfo.put("send", "Send message to another user");
    commandsInfo.put("read", "Read unread messages in inbox");

    severInfo.put("version", SERVER_VERSION);
    severInfo.put("creation date", SERVER_CREATION_DATE);

    startTime = Instant.now();
  }

  public String printGuestOptions() {

    return "Type command: " + "LOGIN," + "REGISTER.";
  }

  public String printAdminOptions() {
    String options =
        "Type command: "
            + "SEND, "
            + "READ_ALL, "
            + "READ_UNREAD, "
            + "DELETE_MESSAGE, "
            + "UPDATE_USER, "
            + "DELETE_USER, "
            + "HELP, "
            + "LOGOUT.";
    return options;
  }

  public String printUserOptions() {
    return "Type command: "
        + "SEND, "
        + "READ_ALL, "
        + "READ_UNREAD, "
        + "DELETE_MESSAGE, "
        + "HELP, "
        + "LOGOUT.";
  }
}
