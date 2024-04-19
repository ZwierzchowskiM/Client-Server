package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ServerResponse {


    Map<String, Integer> uptime = new HashMap<>();
    Map<String, String> commands = new HashMap<>();
    Map<String, String> info = new HashMap<>();
    Server server;

    public ServerResponse(Server server) {
        this.server = server;
    }

    public void calculateUptime(Instant startTime) throws JsonProcessingException {
        Duration uptime = Duration.between(startTime, Instant.now());
        this.uptime.put("hours", uptime.toHoursPart());
        this.uptime.put("minutes", uptime.toMinutesPart());
        this.uptime.put("seconds", uptime.toSecondsPart());
    }

    public void printCommands() {
        commands.put("uptime", "Returns the server's uptime");
        commands.put("info", "Returns the server's version number and creation date");
        commands.put("help", "Returns a list of available commands with a brief description");
        commands.put("stop", "stops both the server and the client simultaneously");
    }


}
