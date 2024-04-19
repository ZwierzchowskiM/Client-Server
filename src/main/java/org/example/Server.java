package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;


public class Server {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Instant startTime;
    static final String SERVER_VERSION = "0.0.1";
    static final String SERVER_CREATION_DATE = "19.04.2024";

    public static void main(String[] args) throws IOException {

        Server server = new Server(6666);
        server.start();
    }

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        startTime = Instant.now();
        System.out.println("Server started on port " + port);
    }

    public void start() {
        try {

            clientSocket = serverSocket.accept();
            System.out.println("Client connected");

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String clientRequest;
            while ((clientRequest = in.readLine()) != null) {
                String response = handleRequest(clientRequest);
                out.println(response);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String handleRequest(String request) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        ServerResponse response = new ServerResponse();

        String serverResponse = switch (request) {
            case "uptime" -> {
                response.calculateUptime(startTime);
                yield mapper.writeValueAsString(response.uptime);
            }
            case "help" -> {
                response.printServerCommands();
                yield mapper.writeValueAsString(response.info);
            }
            case "info" -> {
                response.printServerInfo();
                yield mapper.writeValueAsString(response.info);
            }
            case "stop" -> {
                stopServer();
                yield "server stopped";
            }
            default -> ("command unknown");
        };

        return serverResponse;
    }

    private void stopServer() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Error closing server");
        }
    }
}
