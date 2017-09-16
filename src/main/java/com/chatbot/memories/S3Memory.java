package com.chatbot.memories;

import com.chatbot.amazon.S3Store;
import com.chatbot.models.Message;

import java.util.List;

public class S3Memory implements Memory {


    public static final String S3 = "s3 ";
    public static final String DOWNLOAD = "download ";
    public static final String NAME = "name=";
    public static final String ETAG = "etag ";

    @Override
    public Message getAnswear(Message message) {
        Message result = new Message();

        String words = message.getText().toLowerCase();

        List<String> names = Memory.getValue(words, NAME);
        if (words.contains(" " + S3 + ETAG)) {

            if (names != null && names.size() > 0) {
                String name = names.get(0).replaceAll(NAME, "");
                String etag = S3Store.getTag(name);
                result.setText(etag);
            } else { result.setText("Command not recognized"); }
        } else if (words.contains(" " + S3 + DOWNLOAD)) {
            if (names != null && names.size() > 0) {
                String name = names.get(0).replaceAll(NAME, "");
                String msg = S3Store.download(name);
                result.setText(msg);
            } else { result.setText("Command not recognized"); }


        } else { result.setText("Command not recognized"); }

        return result;
    }


}
