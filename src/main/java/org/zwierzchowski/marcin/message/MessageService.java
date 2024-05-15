package org.zwierzchowski.marcin.message;

import org.zwierzchowski.marcin.user.User;
import org.zwierzchowski.marcin.user.UserDataService;
import org.zwierzchowski.marcin.utils.FileService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageService {

    FileService fileService = new FileService();

    public String sendMessage(String recipent, String content, String sender) throws IOException {

        Message message = new Message(content, sender);
        Map<String, User> users = fileService.loadDataBase();

        if (users.containsKey(recipent)) {
            User user = users.get(recipent);

            if (!checkUserInboxIsFull(user.getMessages())) {
                user.getMessages().add(message);
                fileService.saveDataBase(users);
                return "Message send";
            } else {
                return "Message not send, recipient inbox is full";
            }
        } else {
            return "Recipient not existing";
        }
    }

    public List<Message> getUnreadMessages(String usermame) throws IOException {

        Map<String, User> users = fileService.loadDataBase();
        User user = users.get(usermame);
        List<Message> messages = user.getMessages();
        List<Message> unreadMessages = new ArrayList<>();
        if (!messages.isEmpty()) {
            for (Message m : messages) {
                if (m.getStatus().equals(Message.Status.UNREAD)) {
                    unreadMessages.add(m);
                    m.setStatus(Message.Status.READ);
                }
            }
        }
        fileService.saveDataBase(users);
        return unreadMessages;
    }

    private boolean checkUserInboxIsFull(List<Message> messages) {
        int countUnread = 0;
        for (Message m : messages) {
            if (m.getStatus().equals(Message.Status.UNREAD)) {
                countUnread++;
            }
        }
        return countUnread > 4;
    }
}
