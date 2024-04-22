package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Instant startTime;
    static final String SERVER_VERSION = "0.0.1";
    static final String SERVER_CREATION_DATE = "19.04.2024";

    public static void main(String[] args) {


        Server server = new Server(6666);
        try {
            server.start();
        } catch (RuntimeException e) {
            logger.error("starting connection");
        }
    }

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            logger.error("Error creating server");
            throw new RuntimeException(e);
        }
        startTime = Instant.now();
        logger.info("Server started on port " + port);
    }

    public void start() {
        try {
            clientSocket = serverSocket.accept();
            logger.info("Client connected");

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String clientRequest;
            while ((clientRequest = in.readLine()) != null) {
                String response = handleRequest(clientRequest);
                out.println(response);
            }
        } catch (IOException e) {
            logger.error("Error connecting client");
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
            logger.error("Error closing server");
            throw new RuntimeException(e);
        }
    }
}
