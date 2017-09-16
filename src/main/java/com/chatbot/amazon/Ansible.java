package com.chatbot.amazon;

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Ansible {

    public static void main(String...args2) {
        System.out.println("Hi");

        try {
            System.setProperty("log4j.configuration",
                    new File("/development/amazonclient/src/main/resources/log4j.properties")
                            .toURI().toURL().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        try {
            List<String> args = new ArrayList<String>();

            args.add("54.245.55.36");

            ProcessBuilder pb = new ProcessBuilder("ansible", "54.245.55.36", "-m yum",
                    "-a name=* state=latest");
            Process p = pb.start();

            int status = p.waitFor();
            String result = new BufferedReader(new InputStreamReader(p.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));
            boolean c = false;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public static String runCommand(String ip, String command, String args) {
        String result = null;
        ProcessBuilder pb = new ProcessBuilder("ansible", ip, "-m " + command,
                args!=null ? ("-a " + args) : "" );
        Process p = null;
        try {
            p = pb.start();

            int status = p.waitFor();
            String collect = new BufferedReader(new InputStreamReader(p.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));

            if (collect==null || collect.equals("")) {
                collect = new BufferedReader(new InputStreamReader(p.getErrorStream()))
                        .lines().collect(Collectors.joining("\n"));
            }
            return collect;

        } catch (IOException e) {
            e.printStackTrace();
            result = "";
        } catch (InterruptedException e) {
            e.printStackTrace();
            result = "";
        }

        return  result;

    }


}
