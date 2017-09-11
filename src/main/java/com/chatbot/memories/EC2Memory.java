package com.chatbot.memories;

import com.amazonaws.services.dynamodbv2.xspec.M;
import com.amazonaws.services.ec2.AmazonEC2;
import com.chatbot.MemoryConstants;
import com.chatbot.amazon.EC2;
import com.chatbot.models.Message;

import java.util.List;

public class EC2Memory implements Memory {

    public static final String BOT_EC2 = "bot ec2 ";
    public static final String STOP = "stop";
    public static final String START = "start";
    public static final String DESCRIBE = "describe";
    public static final String REBOOT = "reboot";
    public static final String INSTANCE = "i";
    public static final String AMI = "ami";
    public static final String DASH = "-";
    public static final String RUN = "run";
    public static final String IP = "ip";
    public static final String PROFILE = "profile";
    public static final String REMOVE = "remove";


    @Override
    public Message getAnswear(Message message) {
        Message result = null;

        String words = message.getText().toLowerCase();
        List<String> instances = Memory.getValue(words, INSTANCE + DASH);
        List<String> keys = Memory.getValue(words, "key=");
        List<String> names = Memory.getValue(words, "name=");
        String [] wordsArray = Memory.getValue(words);
        if (words.contains(BOT_EC2 + START)) {
            result = instanceOperation(instances, START);
        } else if (words.contains(BOT_EC2 + STOP)) {
            result = instanceOperation(instances, STOP);
        } else if (words.contains(BOT_EC2 + DESCRIBE)) {
            result = describeEC2();
        } else if (words.contains(BOT_EC2 + RUN)) {
            List<String> amiIds = Memory.getValue(words, AMI + DASH);
            String key = "aws-t2";
            if (keys!=null && keys.size()>0) {
                key = keys.get(0).replaceAll("key=", "");
            }
            String name = "";
            if (names!=null && names.size()>0) {
                name = names.get(0).replaceAll("name=","");
            }
            result = createInstanes(amiIds, key, name);
        } else if (words.contains(BOT_EC2 + AMI)) {
            result = new Message();
            String amiAvailable = "ami-6df1e514";
            result.setText(amiAvailable);
        } else if (words.contains(BOT_EC2 + IP) && wordsArray.length==4)  {
            result = getIp(wordsArray[3]);
        } else if (words.contains(BOT_EC2 + PROFILE) && wordsArray.length==4) {
            result = setProfile(wordsArray[3]);
        }
        else if (words.contains(BOT_EC2 + PROFILE + " " + REMOVE) && wordsArray.length==5) {

            result = removeIamProfile(wordsArray[4]);
        }
        else {
            result = new Message();
            result.setText("Sorry, but dont recogize instanceId specified");
        }


        return result;
    }

    private Message createInstanes(List<String> amiIds, String key, String name) {
        Message result = new Message();
        AmazonEC2 ec2 = EC2.connect();
        amiIds.forEach(ami -> {
            String instanceId = EC2.runI(ec2, ami, key, name);
            result.setText(instanceId + " is requested with ami=" + ami + "\\n");
        });

        ec2.shutdown();
        return result;
    }


    private Message describeEC2() {
        Message result = new Message();
        AmazonEC2 ec2 = EC2.connect();
        result.setText(EC2.describe(ec2));
        ec2.shutdown();
        return result;
    }

    private Message instanceOperation(List<String> instances, String operation) {
        Message result = new Message();
        if (instances.size() > 0) {
            AmazonEC2 ec2 = EC2.connect();
            instances.forEach(instance -> {
                if (STOP.equalsIgnoreCase(operation)) {
                    EC2.stopI(ec2, instance);
                    result.setText(instance + " is stopping...\\n");
                } else if (START.equalsIgnoreCase(operation)) {
                    EC2.startI(ec2, instance);
                    result.setText(instance + " is starting...\\n");
                } else if (REBOOT.equalsIgnoreCase(operation)) {
                    EC2.rebooting(ec2, instance);
                    result.setText(instance + " is rebooting...\\n");
                }
            });

            ec2.shutdown();
        } else {
            result.setText("Sorry, but dont recogize instanceId specified");
        }
        return result;
    }

    public Message getIp(String instanceId) {
        Message result = new Message();
        AmazonEC2 ec2 = EC2.connect();
        result.setText(EC2.getIp(ec2, instanceId));
        ec2.shutdown();
        return result;
    }

    public Message setProfile(String instanceId) {
        Message result = new Message();
        AmazonEC2 ec2 = EC2.connect();
        EC2.setIamProfile(ec2, instanceId);
        result.setText("Profile set for instanceId=" +instanceId);
        ec2.shutdown();
        return result;
    }

    public Message removeIamProfile(String assId) {
        Message result = new Message();
        AmazonEC2 ec2 = EC2.connect();
        EC2.disassociateIam(ec2, assId);
        result.setText("Profile removed for assId=" +assId);
        ec2.shutdown();
        return result;
    }
}
