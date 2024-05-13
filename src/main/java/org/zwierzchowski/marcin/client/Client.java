package org.zwierzchowski.marcin.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Scanner;

@Log4j2
public class Client {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String CLIENT_IP = "127.0.0.1";
    private static final int CLIENT_PORT = 6666;
    private boolean isUserLoggedIn = false;
    private static final ClientNetworkHandler CLIENT_NETWORK_HANDLER = new ClientNetworkHandler();

    public static void main(String[] args) throws IOException {

        Client client = new Client();
        CLIENT_NETWORK_HANDLER.connectToServer(CLIENT_IP, CLIENT_PORT);
        client.communicateServer();
    }


    private void communicateServer() throws IOException {

        String input;
        while (!CLIENT_NETWORK_HANDLER.getSocket().isClosed()) {
            log.info("Choose option");
            input = SCANNER.nextLine();

            handleCommand(input.toLowerCase());
            CLIENT_NETWORK_HANDLER.sendRequest(input);

        }
    }

    public void handleCommand(String command) throws IOException {
        switch (command) {
            case "register" -> handleUserRegistration();
            case "login" -> handleUserLogin();
            case "logout" -> handleUserLogout();
            case "delete" -> handleUserDelete();
            case "send message" -> handleSendMessage();
            case "uptime", "info", "help", "status" -> {
                CLIENT_NETWORK_HANDLER.sendRequest(command);
                String response = CLIENT_NETWORK_HANDLER.receiveResponse();
                CLIENT_NETWORK_HANDLER.printServerResponse(response);
            }
            case "stop" -> {
                CLIENT_NETWORK_HANDLER.sendRequest("stop");
                String response = CLIENT_NETWORK_HANDLER.receiveResponse();
                CLIENT_NETWORK_HANDLER.printServerResponse(response);
                CLIENT_NETWORK_HANDLER.closeConnection();
            }
            default -> log.info("Request unknown");
        }
    }


    private void handleUserRegistration() throws IOException {

        CLIENT_NETWORK_HANDLER.sendRequest("register");

        CLIENT_NETWORK_HANDLER.printServerResponse(CLIENT_NETWORK_HANDLER.receiveResponse());

        log.info("Enter username:");
        String username = SCANNER.nextLine();
        log.info("Enter password:");
        String password = SCANNER.nextLine();
        log.info("Enter role:");
        String role = SCANNER.nextLine();

        CLIENT_NETWORK_HANDLER.sendRequest(username);
        CLIENT_NETWORK_HANDLER.sendRequest(password);
        CLIENT_NETWORK_HANDLER.sendRequest(role);

        String confirmation = CLIENT_NETWORK_HANDLER.receiveResponse();
        log.info(confirmation);
    }

    private void handleUserLogin() throws IOException {

        if (!isUserLoggedIn) {
            CLIENT_NETWORK_HANDLER.sendRequest("login");
            CLIENT_NETWORK_HANDLER.printServerResponse(CLIENT_NETWORK_HANDLER.receiveResponse());

            log.info("Enter username:");
            String username = SCANNER.nextLine();
            log.info("Enter password:");
            String password = SCANNER.nextLine();

            CLIENT_NETWORK_HANDLER.sendRequest(username);
            CLIENT_NETWORK_HANDLER.sendRequest(password);

            String loginResponse = CLIENT_NETWORK_HANDLER.receiveResponse();
            CLIENT_NETWORK_HANDLER.printServerResponse(loginResponse);
            handleLoginResponse(loginResponse);

        } else {
            log.info("User already logged in");
        }
    }

    private void handleUserLogout() throws IOException {

        if (isUserLoggedIn) {
            CLIENT_NETWORK_HANDLER.sendRequest("logout");
            CLIENT_NETWORK_HANDLER.printServerResponse(CLIENT_NETWORK_HANDLER.receiveResponse());
            isUserLoggedIn = false;
        } else {
            log.info("User already not logged in");
        }
    }

    private void handleLoginResponse(String loginResponse) {
        try {
            JsonNode responseNode = OBJECT_MAPPER.readTree(loginResponse);
            String status = responseNode.get("status").asText();

            isUserLoggedIn = "success".equals(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleUserDelete() throws IOException {

        CLIENT_NETWORK_HANDLER.sendRequest("delete");
        CLIENT_NETWORK_HANDLER.printServerResponse(CLIENT_NETWORK_HANDLER.receiveResponse());

        log.info("Enter username:");
        String username = SCANNER.nextLine();

        CLIENT_NETWORK_HANDLER.sendRequest(username);

        String confirmation = CLIENT_NETWORK_HANDLER.receiveResponse();
        log.info(confirmation);
    }

    private void handleSendMessage() throws IOException {

        if (isUserLoggedIn) {
            CLIENT_NETWORK_HANDLER.sendRequest("sendMessage");

            CLIENT_NETWORK_HANDLER.printServerResponse(CLIENT_NETWORK_HANDLER.receiveResponse());
            log.info("Enter recipient:");
            String username = SCANNER.nextLine();
            log.info("Enter message:");
            String message = SCANNER.nextLine();

            CLIENT_NETWORK_HANDLER.sendRequest(username);
            CLIENT_NETWORK_HANDLER.sendRequest(message);

            String confirmation = CLIENT_NETWORK_HANDLER.receiveResponse();
            log.info(confirmation);
        } else {
            log.info("Required to log in");
        }

    }

}

