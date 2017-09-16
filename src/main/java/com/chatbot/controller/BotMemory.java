package com.chatbot.controller;

import com.amazonaws.services.ec2.AmazonEC2;
import com.chatbot.amazon.EC2;
import com.chatbot.memories.*;
import com.chatbot.models.Message;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BotMemory implements Memory {

    public static final String BOT = "bot";
    public static final String BUILD = "build";
    public static final String EC_2 = "ec2";
    public static final String DEPLOY = "deploy";
    public static final String S3 = "s3";
    public static final String ANSIBLE = "ansible";
    public static final String EXECUTE = "execute";
    public static final String DEL = ": ";
    private Map<String, LinkedList<Message>> memories = null;

    private Map<String, Memory> answears = null;

    public BotMemory() {
        this.memories = new ConcurrentHashMap<String, LinkedList<Message>>();
        this.answears = new HashMap<>();
        this.answears.put(BOT, new EC2Memory());
        this.answears.put(BUILD, new BuildMemory());
        this.answears.put(DEPLOY, new DeployMemory());
        this.answears.put(S3, new S3Memory());
        this.answears.put(ANSIBLE, new AnsibleMemory());

    }

    public void addMessage(Message message) {
        getMemory(message.getUser_name() != null
                ? message.getUser_name() : "").add(message);
    }

    public LinkedList<Message> getMemory(String username) {
        LinkedList<Message> memory = memories.get(username);
        if (memory == null) {
            memory = new LinkedList<Message>();
            memories.put(username, memory);
        }
        return memory;
    }

    @Override
    public Message getAnswear(Message message) {

        if (message.getTrigger_word().toLowerCase().equals(BOT)) {
            String botNextWord = message.getText().toLowerCase();
            if (botNextWord.contains(EC_2)) {
                return answears.get(BOT).getAnswear(message);
            } else if (botNextWord.contains(BUILD)) {
                return answears.get(BUILD).getAnswear(message);
            } else if (botNextWord.contains(DEPLOY)) {
                return answears.get(DEPLOY).getAnswear(message);
            } else if (botNextWord.contains(S3)) {
                return answears.get(S3).getAnswear(message);
            } else if (botNextWord.contains(EXECUTE)) {
                return answears.get(ANSIBLE).getAnswear(message);
            } else {
                Message result = new Message();
                result.setText("Unrecognized for bot...");
                return result;
            }

        } else {
            return checkOtherMessages(message);
        }

    }

    private Message searchMemoryForAnwear(Message message) {
        final Message result = new Message();
        LinkedList<Message> memory = getMemory(message.getUser_name());

        if (memory != null) {
            memory.forEach((Message msg) -> {

                if (msg.getPost_id() != message.getPost_id()) {
                    if (msg.getPost_id() != null && msg.getPost_id().contains(
                            message.getText().replaceAll("ss ", ""))) {
                        result.setText(msg.getText());
                        return;
                    } else if (msg.getText() != null && msg.getText().contains(
                            message.getText().replaceAll("ss ", ""))) {
                        result.setText(msg.getText());
                        return;
                    }
                }
            });
        } else {
            result.setText("Memory not found: " + message.getUser_name());
        }
        return result;
    }

    private Message checkOtherMessages(Message message) {
        Message result = new Message();
        switch (message.getTrigger_word().toLowerCase()) {
            case "hi":
                result.setText("Hi, how are you ?");
                break;
            case "how":
                result.setText("I am fine. thanks...");
                break;
            case "time":
                result.setText("It is " + new Date().toString());
                break;
//            case "ec2":
//                AmazonEC2 ec2 = EC2.connect();
//                result.setText(EC2.describe(ec2));
//                ec2.shutdown();
//                break;
            case "version":
                result.setText("1.0.2");
                break;
            case "mh":
            case "memory":
                List<String> names = Memory.getValue(message.getText(), "name=");
                String name = null;
                if (names != null && names.size() > 0) {
                    name = names.get(0).replaceAll("name=", "");
                } else {
                    name = message.getUser_name();
                }
                List<Message> mems = memories.get(name);
                StringBuffer historyResult = new StringBuffer();
                mems.forEach(mem -> {

                    historyResult.append(mem.getPost_id() + DEL + mem.getUser_name()
                            + DEL + mem.getTimestamp() + DEL + mem.getText() + "\n");


                });
                result.setText(historyResult.toString());


                break;
            case "ss":
                result = searchMemoryForAnwear(message);
                break;
            case "help":
                String text = "hi|how are you|time|\n" +
                        "bot ec2 ami\n" +
                        "bot ec2 describe\n" +
                        "bot ec2 run ami-6df1e514 name=lolobot\n" +
                        "bot ec2 ip i-xxx\n" +
                        "bot ec2 start/stop i-xxx\n" +
                        "bot s3 etag name=johny\n" +
                        "bot s3 download name=johny\n" +
                        "bot build name=lolobot repo=https://xxx\n" +
                        "bot build start name=lolobot\n" +
                        "bot build name=lalala8 repo=https://github.com/dawidor/acs type=github\n" +
                        "bot deploy create lolobot lolobot\n" +
                        "bot deploy run lolobot etag=089dd25825793eef9c3377b1c6aa1713\n" +
                        "bot ec2 profile i-06ce8c6f7a49d9acd\n" +
                        "bot ec2 profle remove AIPAJOHU6GK7GY56OLAZ4\n" +
                        "bot execute ip=xxx command=yum args=name=* state=latest\n" +
                        "memory history name=x (mh)\n" +
                        "ss postId\n";

                result.setText(text);
                break;
            default:
                result.setText("Sorry. I am too stupid to answer your question... :(");
                break;
        }
        return result;
    }


}
