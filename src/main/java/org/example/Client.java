package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static final Logger logger = LogManager.getLogger(Client.class);
    private static final Scanner scanner = new Scanner(System.in);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String CLIENT_IP = "127.0.0.1";
    private static final int CLIENT_PORT = 6666;

    public static void main(String[] args) {

        Client client = new Client();
        try {
            client.startConnection(CLIENT_IP, CLIENT_PORT);
        } catch (RuntimeException e) {
            logger.error("connection error" + e.getMessage());
        }
    }

    public void startConnection(String ip, int port) {
        try (
                Socket clientSocket = new Socket(ip, port);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            logger.info("Client connected to server");

            String input;
            while (!clientSocket.isClosed()) {
                logger.info("Type what you want to do ");
                input = scanner.nextLine();
                switch (input) {
                    case "uptime", "info", "help" -> sendMessage(out, in, input);
                    case "stop" -> {
                        sendMessage(out, in, "stop");
                        stopConnection();
                        return;
                    }
                    default -> logger.info("request unknown");
                }
            }
        } catch (IOException e) {
            logger.error("Error connecting to server: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void sendMessage(PrintWriter out, BufferedReader in, String msg) {
        out.println(msg);
        try {
            String resp = in.readLine();
            handleResponse(resp);
        } catch (IOException e) {
            logger.error("Error reading message from server: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void handleResponse(String jsonResp) {
        try {
            JsonNode rootNode = mapper.readTree(jsonResp);
            String prettyString = rootNode.toPrettyString();
            logger.info(prettyString);
        } catch (IOException e) {
            System.out.println("Error processing JSON response: " + e.getMessage());
        }
    }

    public void stopConnection() {
            logger.info("connection stopped");
    }
}