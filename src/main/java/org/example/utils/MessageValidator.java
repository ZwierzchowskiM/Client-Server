package org.example.utils;

import java.util.regex.Pattern;

public class MessageValidator {

    private static final int MESSAGE_LENGHT  = 255;

    public static void validateMessage(String content) {
        if (content == null || content.length()>255) {
            throw new IllegalArgumentException("Invalid message format");
        }
    }
}
