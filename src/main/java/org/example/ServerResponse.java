package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ServerResponse {

    Map<String, Integer> uptime = new HashMap<>();
    Map<String, String> info = new HashMap<>();
    Server server;

    public ServerResponse() {
    }

    public void calculateUptime(Instant startTime) throws JsonProcessingException {
        Duration uptime = Duration.between(startTime, Instant.now());
        this.uptime.put("hours", uptime.toHoursPart());
        this.uptime.put("minutes", uptime.toMinutesPart());
        this.uptime.put("seconds", uptime.toSecondsPart());
    }

    public void printServerCommands() {
        info.put("uptime", "Returns the server's uptime");
        info.put("info", "Returns the server's version number and creation date");
        info.put("help", "Returns a list of available commands with a brief description");
        info.put("stop", "stops both the server and the client simultaneously");
    }

    public void printServerInfo() {
        info.put("version", "1.0.0");
        info.put("creation date", "19.04.2024");
    }
}
