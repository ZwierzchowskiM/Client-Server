package org.example.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static final Logger logger = LogManager.getLogger(Client.class);
    private static final Scanner scanner = new Scanner(System.in);
    private static final ObjectMapper mapper = new ObjectMapper();
    static final String CLIENT_IP = "127.0.0.1";
    static final int CLIENT_PORT = 6666;
    private boolean isUserLoggedIn = false;
    private static final ClientNetworkHandler clientNetworkHandler = new ClientNetworkHandler();

    public static void main(String[] args) throws IOException {

        Client client = new Client();
        clientNetworkHandler.connectToServer(CLIENT_IP, CLIENT_PORT);
        client.communicateServer();
    }


    private void communicateServer() throws IOException {

        String input;
        while (!clientNetworkHandler.getSocket().isClosed()) {
            logger.info("Choose option");
            input = scanner.nextLine();
            handleCommand(input);
        }
    }

    public void handleCommand(String command) throws IOException {
        switch (command) {
            case "register" -> handleUserRegistration();
            case "login" -> handleUserLogin();
            case "logout" -> handleUserLogout();
            case "delete" -> handleUserDelete();
            case "uptime", "info", "help", "status" -> {
                clientNetworkHandler.sendRequest(command);
                String response = clientNetworkHandler.receiveResponse();
                clientNetworkHandler.printServerResponse(response);
            }
            case "stop" -> {
                clientNetworkHandler.sendRequest("stop");
                String response = clientNetworkHandler.receiveResponse();
                clientNetworkHandler.printServerResponse(response);
                clientNetworkHandler.closeConnection();
            }
            default -> logger.info("Request unknown");
        }
    }


    private void handleUserRegistration() throws IOException {

        clientNetworkHandler.sendRequest("register");

        clientNetworkHandler.printServerResponse(clientNetworkHandler.receiveResponse());

        logger.info("Enter username:");
        String username = scanner.nextLine();
        logger.info("Enter password:");
        String password = scanner.nextLine();
        logger.info("Enter role:");
        String role = scanner.nextLine();

        clientNetworkHandler.sendRequest(username);
        clientNetworkHandler.sendRequest(password);
        clientNetworkHandler.sendRequest(role);

        String confirmation = clientNetworkHandler.receiveResponse();
        System.out.println(confirmation);
    }

    private void handleUserLogin() throws IOException {

        if (!isUserLoggedIn) {
            clientNetworkHandler.sendRequest("login");
            clientNetworkHandler.printServerResponse(clientNetworkHandler.receiveResponse());

            logger.info("Enter username:");
            String username = scanner.nextLine();
            logger.info("Enter password:");
            String password = scanner.nextLine();

            clientNetworkHandler.sendRequest(username);
            clientNetworkHandler.sendRequest(password);

            String loginResponse = clientNetworkHandler.receiveResponse();
            clientNetworkHandler.printServerResponse(loginResponse);
            handleLoginResponse(loginResponse);

        } else {
            logger.info("User already logged in");
        }
    }

    private void handleUserLogout() throws IOException {

        if (isUserLoggedIn) {
            clientNetworkHandler.sendRequest("logout");
            clientNetworkHandler.printServerResponse(clientNetworkHandler.receiveResponse());
            isUserLoggedIn = false;
        } else {
            logger.info("User already not logged in");
        }
    }

    private void handleLoginResponse(String loginResponse) {
        try {
            JsonNode responseNode = mapper.readTree(loginResponse);
            String status = responseNode.get("status").asText();

            isUserLoggedIn = "success".equals(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleUserDelete() throws IOException {

        clientNetworkHandler.sendRequest("delete");
        clientNetworkHandler.printServerResponse(clientNetworkHandler.receiveResponse());

        logger.info("Enter username:");
        String username = scanner.nextLine();

        clientNetworkHandler.sendRequest(username);

        String confirmation = clientNetworkHandler.receiveResponse();
        System.out.println(confirmation);
    }

}
