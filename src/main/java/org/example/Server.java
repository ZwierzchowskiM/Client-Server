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
    private UserDataService userDataService;
    PrintWriter out ;
    BufferedReader in ;

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            logger.error("Error creating server" + e.getMessage());
            throw new RuntimeException(e);
        }
        serverData = new ServerData();
        userDataService = new UserDataService();
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
        try  {

            Socket clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            logger.info("Client connected");
            String clientRequest;
            while ((clientRequest = in.readLine()) != null) {
                logger.info("Client request: " + clientRequest);
                String response = handleRequest(clientRequest);
                sendMessageClient(response);
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
                case "register" -> handleRegistration();
                case "uptime" -> response.calculateUptime(startTime);
                case "help" -> response.printServerCommands(serverData.getCommandInfo());
                case "info" -> response.printServerInfo(serverData.getServerInfo());
                case "stop" -> stopServer();
                default -> ("{\"info\": \"command unknown\"}");
            };
            return serverResponse;
        } catch (IOException e) {
            logger.error("Error in generating JSON response");
            return "{\"error\": \"Internal server error\"}";
        }
    }


    private String handleRegistration() throws IOException {
        ServerResponse response = new ServerResponse();
        String infoReg = "{\"request\": \"Please provide username and password\"}";
        sendMessageClient(infoReg);

        String username = in.readLine();
        String password = in.readLine();
        String role = in.readLine();

        User registeredUser =  userDataService.addUser(username, password, role);

        return response.registerUser(registeredUser);
    }

    private void sendMessageClient(String msg) {
        out.println(msg);
    }

    private String stopServer() {
        try {
            serverSocket.close();
            return "{\"info\": \"server stopped\"}";
        } catch (IOException e) {
            logger.error("Error closing server" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
