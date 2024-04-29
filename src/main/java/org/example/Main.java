package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws JsonProcessingException {

        UserDataService userDataService = new UserDataService();
        NormalUser user1 = new NormalUser("m773", "123");
        User user2 = new Admin("m774", "124");
        List<User> users = List.of(user1, user2);


        try {
            userDataService.addUser(user1);
            userDataService.addUser(user2);
            System.out.println(userDataService.loadUsers());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
