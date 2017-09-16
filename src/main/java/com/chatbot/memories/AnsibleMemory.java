package com.chatbot.memories;

import com.chatbot.MemoryConstants;
import com.chatbot.amazon.Ansible;
import com.chatbot.models.Message;
import com.fasterxml.jackson.databind.util.JSONPObject;

import java.text.StringCharacterIterator;
import java.util.List;

public class AnsibleMemory implements Memory {


    public static final String COMMAND = "command=";
    public static final String ARGS = "args=";
    public static final String EXECUTE = "execute";
    public static final String IP = "ip=";

    @Override
    public Message getAnswear(Message message) {
        Message result = new Message();

        String words = message.getText().toLowerCase();
        List<String> commands = Memory.getValue(words, COMMAND);
        String args = "";
        if (words.indexOf("args=")>-1) {
            args = words.substring(words.indexOf(ARGS) + 5);
        }
        List<String> ips = Memory.getValue(words, IP);

        if (words.contains(MemoryConstants.BOT + EXECUTE)) {
            if (commands!=null && commands.size() > 0) {
                String command = commands.get(0).replaceAll(COMMAND, "");
                String ip = "";
                if (ips !=null && ips.size()>0) {
                    ip = ips.get(0).replaceAll(IP, "");

                    String msg = Ansible.runCommand(ip, command, args);

                    result.setText(msg);
                } else {

                    result.setText("Incorrect ip");
                }


            } else {
                result.setText("Incorrect command");
            }
        }

        return result;
    }


}
