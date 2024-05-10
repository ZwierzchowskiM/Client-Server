package org.example.message;

import org.example.user.User;
import org.example.user.UserDataService;

import java.io.IOException;
import java.util.Map;

public class MessageService {

    UserDataService userDataService = new UserDataService();

    public String sendMessage(String recipent, String content ,String sender) throws IOException {

        Message message = new Message(content, sender);

        Map<String, User> users = userDataService.loadUsers();
        User user = users.get(recipent);
        user.getMessages().add(message);
        userDataService.saveUsers(users);

        return "Message send";
    }
}
