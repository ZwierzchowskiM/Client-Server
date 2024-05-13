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
    private static final ClientNetworkHandler CLIENT_NETWORK_HANDLER = new ClientNetworkHandler();

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.communicateServer();
    }

    public Client() {
        CLIENT_NETWORK_HANDLER.connectToServer(CLIENT_IP, CLIENT_PORT);
    }

    private void communicateServer() throws IOException {
        while (!CLIENT_NETWORK_HANDLER.getSocket().isClosed()) {
            handleServerResponse();
            if (SCANNER.hasNextLine()) {
                String input = SCANNER.nextLine();
                handleUserInput(input);
            }
        }
    }

    private void handleServerResponse() throws IOException {
        Optional<String> responseOpt = CLIENT_NETWORK_HANDLER.receiveResponse();
        responseOpt.ifPresent(CLIENT_NETWORK_HANDLER::printServerResponse);
    }

    private void handleUserInput(String input) throws IOException {
        CLIENT_NETWORK_HANDLER.sendRequest(input);
        if ("stop".equalsIgnoreCase(input)) {
            Optional<String> responseOpt = CLIENT_NETWORK_HANDLER.receiveResponse();
            responseOpt.ifPresent(CLIENT_NETWORK_HANDLER::printServerResponse);
            CLIENT_NETWORK_HANDLER.closeConnection();
        }
    }
}

