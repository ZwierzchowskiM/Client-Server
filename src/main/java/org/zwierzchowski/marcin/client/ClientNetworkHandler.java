package org.zwierzchowski.marcin.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;

@Log4j2
@Getter
public class ClientNetworkHandler {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final ObjectMapper mapper = new ObjectMapper();

    public void connectToServer(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            log.info("Connected to server at {} : {}", ip, port);
        } catch (IOException e) {
            log.error("connection error {}", e.getMessage());
        }
    }

    public void sendRequest(String request) {
        out.println(request);
    }

    public String receiveResponse() throws IOException {
            String response = in.readLine();
            return response;
    }

    public boolean hasResponse() throws IOException {
        return in.ready();
    }

    public void closeConnection()  {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        closeResources();
        log.info("Disconnected from server");
    }

    public void printServerResponse(String jsonResp) {
        try {
            JsonNode rootNode = mapper.readTree(jsonResp);
            String prettyString = rootNode.toPrettyString();
            log.info(prettyString);
        } catch (IOException e) {
            log.error("Error processing JSON response: {}", e.getMessage());
        }
    }

    private void closeResources() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            log.error("Failed to close resources: {}", e.getMessage());
        }
    }

}
