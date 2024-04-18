package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;

public class Server {


    //    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Instant startTime;

    public static void main(String[] args) throws IOException {

        Server server = new Server();
        server.start(6666);
    }

    public void start(int port) {
        try (
                ServerSocket serverSocket = new ServerSocket(port)) {
            startTime = Instant.now();
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("connected");

            String inputLine;
            while ((inputLine = in.readLine()) != null) {

                String translation = switch (inputLine) {
                    case "uptime" -> getUptime();
                    default -> "unknown";
                };
                out.println(translation);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUptime() throws JsonProcessingException {
        Duration uptime = Duration.between(startTime, Instant.now());
        ServerResponse response = new ServerResponse(uptime.toHoursPart(), uptime.toMinutesPart(), uptime.toSecondsPart());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(response);

        return jsonResponse;
    }


}
