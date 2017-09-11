package com.chatbot.memories;

import com.chatbot.models.Message;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface Memory {

    Message getAnswear(Message message);

    public static List<String> getValue(String words, String key) {
        return Arrays.stream(words.split(" "))
                .filter(str -> str.contains(key))
                .collect(Collectors.toList());
    }

    public static String [] getValue(String words) {
        return words.split(" ");

    }
}
