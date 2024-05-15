package org.zwierzchowski.marcin.server;

import java.util.HashMap;
import java.util.Map;

public class ServerData {

    private final Map<String, String> commandsInfo = new HashMap<>();
    private final Map<String, String> severInfo = new HashMap<>();
    private static final String SERVER_VERSION = "0.1.0";
    private static final String SERVER_CREATION_DATE = "19.04.2024";

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

        severInfo.put("version", SERVER_VERSION);
        severInfo.put("creation date", SERVER_CREATION_DATE);
    }

    public Map<String, String> getCommandInfo() {
        return commandsInfo;
    }

    public Map<String, String> getServerInfo() {
        return severInfo;
    }
}
