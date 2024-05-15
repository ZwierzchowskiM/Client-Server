package org.zwierzchowski.marcin.utils;

public class MessageValidator {

    private static final int MESSAGE_LENGHT  = 255;

    public static void validateMessage(String content) {
        if (content == null || content.length()>MESSAGE_LENGHT) {
            throw new IllegalArgumentException("Invalid message format");
        }
    }
}
