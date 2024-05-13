package org.zwierzchowski.marcin.client;

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
        client.communicateServer();
    }

    public Client() {
        CLIENT_NETWORK_HANDLER.connectToServer(CLIENT_IP, CLIENT_PORT);
    }

    private void communicateServer() throws IOException {

        String response;
        String input;
        while (!CLIENT_NETWORK_HANDLER.getSocket().isClosed()) {

            response = CLIENT_NETWORK_HANDLER.receiveResponse();

            if (response != null) {
                CLIENT_NETWORK_HANDLER.printServerResponse(response);
            }

            input = SCANNER.nextLine();
            CLIENT_NETWORK_HANDLER.sendRequest(input);
        }
    }
}

