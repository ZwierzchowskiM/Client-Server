package org.zwierzchowski.marcin.client;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

@Log4j2
public class Client {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final String CLIENT_IP = "127.0.0.1";
    private static final int CLIENT_PORT = 6666;
    private final ClientNetworkHandler clientNetworkHandler = new ClientNetworkHandler();

    public static void main(String[] args) {
        Client client = new Client();
        client.communicateServer();
    }

    public Client() {
        clientNetworkHandler.connectToServer(CLIENT_IP, CLIENT_PORT);
    }

    private void communicateServer() {
        while (!clientNetworkHandler.getSocket().isClosed()) {
            try {
                handleServerResponse();
                if (SCANNER.hasNextLine()) {
                    String input = SCANNER.nextLine();
                    handleUserInput(input);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleServerResponse() throws IOException {
        Optional<String> responseOpt = clientNetworkHandler.receiveResponse();
        responseOpt.ifPresent(clientNetworkHandler::printServerResponse);
    }

    private void handleUserInput(String input) throws IOException {
        clientNetworkHandler.sendRequest(input);
        if (input.equalsIgnoreCase("stop")) {
            Optional<String> responseOpt = clientNetworkHandler.receiveResponse();
            responseOpt.ifPresent(clientNetworkHandler::printServerResponse);
            clientNetworkHandler.closeConnection();
        }
    }
}

