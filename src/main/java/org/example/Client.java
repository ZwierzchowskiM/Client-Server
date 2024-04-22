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

    private static Logger logger = LogManager.getLogger(Client.class);

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private final Scanner scanner = new Scanner(System.in);
    private static final String CLIENT_IP = "127.0.0.1";
    private static final int CLIENT_PORT = 6666;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {

        Client client = new Client();
        try {
            client.startConnection(CLIENT_IP, CLIENT_PORT);
        } catch (RuntimeException e) {
            logger.error("connection error");
        }
    }

    public void startConnection(String ip, int port) {

        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            logger.info("Client connected to server");
        } catch (IOException e) {
            logger.error("error connecting to server");
            throw new RuntimeException(e);
        }

        while (clientSocket.isConnected()) {
            String input = scanner.nextLine();
            switch (input) {
                case "uptime", "info", "help" -> sendMessage(input);
                case "stop" -> {
                    sendMessage("stop");
                    stopConnection();
                }
                default -> logger.info("request unknown");
            }
        }
    }

    public void sendMessage(String msg) {
        out.println(msg);
        try {
            String resp = in.readLine();
            handleResponse(resp);
        } catch (IOException e) {
            logger.error("error reading message from server");
            throw new RuntimeException(e);
        }
    }

    public void handleResponse(String jsonResp) {
        try {
            JsonNode rootNode = mapper.readTree(jsonResp);
            String prettyString = rootNode.toPrettyString();
            logger.error(prettyString);
        } catch (IOException e) {
            System.out.println("Error processing JSON response: " + e.getMessage());
        }
    }

    public void stopConnection() {

        try {
            in.close();
            out.close();
            clientSocket.close();
            logger.info("connection stopped");
        } catch (IOException e) {
            logger.error("error stopping connection");
            throw new RuntimeException(e);
        }
    }
}