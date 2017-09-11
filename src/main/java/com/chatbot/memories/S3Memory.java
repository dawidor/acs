package com.chatbot.memories;

import com.chatbot.amazon.S3Store;
import com.chatbot.models.Message;

import java.util.List;

public class S3Memory implements Memory {


    @Override
    public Message getAnswear(Message message) {
        Message result = new Message();

        String words = message.getText().toLowerCase();

        List<String> names = Memory.getValue(words, "name=");
        if (words.contains(" s3 ")) {

            if (names!=null && names.size()>0) {
                String name = names.get(0).replaceAll("name=", "");
                String etag = S3Store.getTag(name);
                result.setText(etag);
            }

        } else {
            result.setText("Command not recognized...");
        }

        return result;
    }


}
