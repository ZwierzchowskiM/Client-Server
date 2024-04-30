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
    static final String CLIENT_IP = "127.0.0.1";
    static final int CLIENT_PORT = 6666;

    public static void main(String[] args) {

        Client client = new Client();
        try {
            client.start(CLIENT_IP, CLIENT_PORT);
        } catch (RuntimeException e) {
            logger.error("connection error" + e.getMessage());
        }
    }

    public void start(String ip, int port) {
        try (
                Socket clientSocket = new Socket(ip, port);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            logger.info("Client connected to server");

            String input;
            while (!clientSocket.isClosed()) {
                logger.info(printOptions());
                input = scanner.nextLine();
                switch (input) {
                    case "register" -> {
                        messageServer(out,in,input);
                        handleRegistrationProcess(in,out);
                    }
                    case "uptime", "info", "help" -> messageServer(out, in, input);
                    case "stop" -> {
                        messageServer(out, in, "stop");
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

    private void messageServer(PrintWriter out, BufferedReader in, String msg) {
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
            logger.error("Error processing JSON response: " + e.getMessage());
        }
    }

    public void stopConnection() {
        logger.info("connection stopped");
    }

    private String printOptions() {
        return "Choose an option: register,uptime,info,help,stop";
    }

    private void handleRegistrationProcess(BufferedReader in, PrintWriter out) throws IOException {
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();
        System.out.println("Enter role:");
        String role = scanner.nextLine();

        out.println(username);
        out.println(password);
        out.println(role);

        String confirmation = in.readLine();
        System.out.println(confirmation);
    }
}