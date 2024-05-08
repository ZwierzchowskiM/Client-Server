package org.example.client;

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
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    public static void main(String[] args) throws IOException {

        Client client = new Client();
        client.connectToServer(CLIENT_IP, CLIENT_PORT);
        client.communicateServer();
    }

    private void connectToServer(String ip, int port) {
        try {
            socket = new Socket(ip, port);
        } catch (IOException e) {
            logger.error("connection error" + e.getMessage());
        }
        logger.info("Connected to server at " + ip + ":" + port);
    }

    private void communicateServer() throws IOException {

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String input;
        while (!socket.isClosed()) {
            logger.info(printOptions());
            input = scanner.nextLine();
            handleCommand(input);
        }
    }

    public void handleCommand(String command) throws IOException {
        switch (command) {
            case "register" -> handleUserRegistration();
            case "login" -> handleUserLogin();
            case "delete" -> handleUserDelete();
            case "uptime", "info", "help", "status" -> {
                requestServer(command);
                String response = getServerResponse();
                handleResponse(response);
            }
            case "stop" -> {
                requestServer("stop");
                String response = getServerResponse();
                handleResponse(response);
                stopConnection();
            }
            default -> logger.info("Request unknown");
        }
    }

    private void requestServer(String message) {
        out.println(message);
    }

    private String getServerResponse() throws IOException {
        return in.readLine();
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

    public void stopConnection() throws IOException {
        socket.close();
        logger.info("Disconnected from server");
    }

    private String printOptions() {
        return "Choose an option: status,login,register,uptime,info,help,stop";
    }

    private void handleUserRegistration() throws IOException {

        requestServer("register");

        handleResponse(getServerResponse());

        logger.info("Enter username:");
        String username = scanner.nextLine();
        logger.info("Enter password:");
        String password = scanner.nextLine();
        logger.info("Enter role:");
        String role = scanner.nextLine();

        out.println(username);
        out.println(password);
        out.println(role);

        String confirmation = in.readLine();
        System.out.println(confirmation);
    }

    private void handleUserLogin() throws IOException {

        requestServer("login");

        handleResponse(getServerResponse());

        logger.info("Enter username:");
        String username = scanner.nextLine();
        logger.info("Enter password:");
        String password = scanner.nextLine();

        out.println(username);
        out.println(password);

        String confirmation = in.readLine();
        System.out.println(confirmation);
    }

    private void handleUserDelete() throws IOException {

        requestServer("delete");

        handleResponse(getServerResponse());

        logger.info("Enter username:");
        String username = scanner.nextLine();

        out.println(username);

        String confirmation = in.readLine();
        System.out.println(confirmation);
    }



}
