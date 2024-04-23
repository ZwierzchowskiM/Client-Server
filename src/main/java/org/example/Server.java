package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;


public class Server {

    private static Logger logger = LogManager.getLogger(Server.class);
    private ServerSocket serverSocket;
    private Instant startTime;
    private ServerData serverData;

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            logger.error("Error creating server" + e.getMessage());
            throw new RuntimeException(e);
        }
        serverData = new ServerData();
        startTime = Instant.now();
        logger.info("Server started on port " + port);
    }

    public static void main(String[] args) {

        Server server = new Server(6666);
        try {
            server.start();
        } catch (RuntimeException e) {
            logger.error("starting connection");
        }
    }

    public void start() {
        try (
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))

        ) {

            logger.info("Client connected");
            String clientRequest;
            while ((clientRequest = in.readLine()) != null) {
                String response = handleRequest(clientRequest);
                out.println(response);
            }
        } catch (IOException e) {
            logger.error("Error connecting client" + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public String handleRequest(String request) throws JsonProcessingException {

        ServerResponse response = new ServerResponse();

        try {
            String serverResponse = switch (request) {
                case "uptime" -> response.calculateUptime(startTime);
                case "help" -> response.printServerInfo(serverData.getServerInfo());
                case "info" -> response.printServerCommands(serverData.getCommandInfo());
                case "stop" -> {
                    stopServer();
                    yield "server stopped";
                }
                default -> ("command unknown");
            };
            return serverResponse;
        } catch (JsonProcessingException e) {
            logger.error("Error in generating JSON response");
            return "{\"error\": \"Internal server error\"}";
        }
    }

    private void stopServer() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.error("Error closing server" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
