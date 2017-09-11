package com.chatbot.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Posts {

    private String [] order;


    public String[] getOrder() {
        return order;
    }

    public void setOrder(String[] order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "Posts{" +
                "order=" + Arrays.toString(order) +
                '}';
    }
}
