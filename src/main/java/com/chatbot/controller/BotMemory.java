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
    private Map<String, LinkedList<Message>> memories = null;

    private Map<String, Memory> answears = null;

    public BotMemory() {
        this.memories = new ConcurrentHashMap<String, LinkedList<Message>>();
        this.answears = new HashMap<>();
        this.answears.put(BOT, new EC2Memory());
        this.answears.put(BUILD, new BuildMemory());
        this.answears.put(DEPLOY, new DeployMemory());
        this.answears.put(S3, new S3Memory());

    }

    public void addMessage(Message message) {
        LinkedList<Message> memory = getMemory(message.getUser_name());
        memory.add(message);
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
        LinkedList<Message> memory = getMemory(message.getUser_name());
        Iterator<Message> iter = memory.iterator();

        while (iter.hasNext()) {
            Message msg = iter.next();
            if (message.getUser_name().equals(msg.getUser_name())) {
                if (message.getText().replaceAll(" ", "")
                        .equalsIgnoreCase(msg.getText().replaceAll(" ", ""))) {
                    return iter.next();
                }
            }
        }
        return null;
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
            case "ec2":
                AmazonEC2 ec2 = EC2.connect();
                result.setText(EC2.describe(ec2));
                ec2.shutdown();

            case "version":
                result.setText("1.0.1");
                break;


            case "help":
                result.setText("hi|how are you|what time|describe ec2" +
                        "bot ec2 ami\n" +
                        "dr4aa\n" +
                        "12:00 AM\n" +
                        "  \n" +
                        "\n" +
                        "ami-6df1e514\n" +
                        "dawidor\n" +
                        "12:04 AM\n" +
                        "  \n" +
                        "\n" +
                        "bot ec2 run ami-6df1e514 name=lolobot\n" +
                        "dr4aa\n" +
                        "12:04 AM\n" +
                        "  \n" +
                        "\n" +
                        "r-02baa48b70e07ffed is requested with ami=ami-6df1e514\n" +
                        "dawidor\n" +
                        "12:05 AM\n" +
                        "  \n" +
                        "\n" +
                        "bot build name=lolobot repo=https://git-codecommit.us-west-2.amazonaws.com/v1/repos/acs\n" +
                        "dr4aa\n" +
                        "12:05 AM\n" +
                        "  \n" +
                        "\n" +
                        "Project lolobot created...{Name: lolobot,Arn: arn:aws:codebuild:us-west-2:894794566272:project/lolobot,Source: {Type: CODECOMMIT,Location: https://git-codecommit.us-west-2.amazonaws.com/v1/repos/acs,Auth: {Type: OAUTH,}},Artifacts: {Type: S3,Location: arn:aws:s3:::aws-codestar-us-west-2-894794566272,NamespaceType: NONE,Name: lolobot,Packaging: ZIP},Environment: {Type: LINUX_CONTAINER,Image: aws/codebuild/java:openjdk-8,ComputeType: BUILD_GENERAL1_SMALL,EnvironmentVariables: [],PrivilegedMode: false},ServiceRole: arn:aws:iam::894794566272:role/CodeStarWorker-acs-CodeBuild,TimeoutInMinutes: 60,EncryptionKey: arn:aws:kms:us-west-2:894794566272:alias/aws/s3,Created: Sun Sep 10 22:05:13 UTC 2017,LastModified: Sun Sep 10 22:05:13 UTC 2017}\n" +
                        "dawidor\n" +
                        "12:05 AM\n" +
                        "  \n" +
                        "\n" +
                        "bot build start name=lolobot\n" +
                        "dr4aa\n" +
                        "12:05 AM\n" +
                        "  \n" +
                        "\n" +
                        "Build lolobot created...{Id: lolobot:dc49a7f6-230b-46e3-a27a-37aef393b229,Arn: arn:aws:codebuild:us-west-2:894794566272:build/lolobot:dc49a7f6-230b-46e3-a27a-37aef393b229,StartTime: Sun Sep 10 22:05:31 UTC 2017,CurrentPhase: SUBMITTED,BuildStatus: IN_PROGRESS,ProjectName: lolobot,Source: {Type: CODECOMMIT,Location: https://git-codecommit.us-west-2.amazonaws.com/v1/repos/acs,Auth: {Type: OAUTH,}},Artifacts: {Location: arn:aws:s3:::aws-codestar-us-west-2-894794566272/lolobot,},Environment: {Type: LINUX_CONTAINER,Image: aws/codebuild/java:openjdk-8,ComputeType: BUILD_GENERAL1_SMALL,EnvironmentVariables: [],PrivilegedMode: false},TimeoutInMinutes: 60,BuildComplete: false,Initiator: dawidor}\n" +
                        "dawidor\n" +
                        "12:06 AM\n" +
                        "  \n" +
                        "\n" +
                        "bot deploy create lolobot lolobot\n" +
                        "dr4aa\n" +
                        "12:06 AM\n" +
                        "  \n" +
                        "\n" +
                        "Application to deploy created. You can run deployment now...\n" +
                        "dawidor\n" +
                        "12:08 AM\n" +
                        "  \n" +
                        "\n" +
                        "bot deploy run lolobot etag=089dd25825793eef9c3377b1c6aa1713");
                break;
            default:
                result.setText("Sorry. I am too stupid to answer your question... :(");
                break;
        }
        return result;
    }


}
