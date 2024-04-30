package org.example;

import java.util.HashMap;
import java.util.Map;

public class ServerData {

    private final Map<String, String> commandsInfo = new HashMap<>();
    private final Map<String, String> severInfo = new HashMap<>();
    static final String SERVER_VERSION = "0.1.0";
    static final String SERVER_CREATION_DATE = "19.04.2024";

    public ServerData() {
        initialize();
    }

    private void initialize() {
        commandsInfo.put("uptime", "Returns the server's uptime");
        commandsInfo.put("info", "Returns the server's version number and creation date");
        commandsInfo.put("help", "Returns a list of available commands with a brief description");
        commandsInfo.put("stop", "stops both the server and the client simultaneously");
        commandsInfo.put("addUser", "Add new user");

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
