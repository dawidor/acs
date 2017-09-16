package com.chatbot.controller;

import com.chatbot.models.*;
import org.apache.log4j.Logger;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class MattermostService {

    private RestTemplate restTemplate = null;

   // private String ip = "localhost";
    private String port = "80";
    private String ip = "35.166.77.80";


    final String baseURL = "http://" + ip + ":8065/api/v4";

    private static final Logger log = Logger.getLogger(MattermostService.class);
    private String token = null;


    public MattermostService() {
        this.restTemplate = new RestTemplate();
    }

    private HttpHeaders getHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set("Authorization", "Bearer " + token);
        return headers;
    }

    public String createWebhook(String teamId, String channelId,
                                String callbackParh,
                                List<String> words) {
        String url = "/hooks/outgoing";

        String json = "{\n" +
                "\n" +
                "    \"team_id\": \"" + teamId + "\",\n" +
                "    \"channel_id\": \"" + channelId + "\",\n" +
                "    \"description\": \"test\",\n" +
                "    \"display_name\": \"test2\",\n" +
                "    \"trigger_words\": \n" +
                "\n" +
                "[\n" +
                "\n";
        for (int i = 0; i < words.size(); i++) {
                    if (i>0) {
                        json += ",\n";
                    }
                    String word = words.get(i);
                    json += "\"" + word + "\"";

                }
                //"    \"hi\"\n" +
                json += "\n" +
                "],\n" +
                "\"trigger_when\": 0,\n" +
                "\"callback_urls\": \n" +
                "\n" +
                "    [\n" +
             //   "        \"http://35.166.77.80/\"\n" +
                "        \"http://" + ip + ":" + port + "/\"\n" +
                "    ],\n" +
                "    \"content_type\": \"application/json\"\n" +
                "\n" +
                "}";



        HttpEntity<String> entity = new HttpEntity<String>(json, getHeader());
        ResponseEntity<String> result = restTemplate.exchange(baseURL + url, HttpMethod.POST,
                entity, String.class);
        return result.getBody();
    }

    public Webhook[] listWebhook(String teamId, String channelId) {
        String url = "/hooks/outgoing?team_id=" + teamId + "&channel_id=" + channelId;
        HttpEntity<String> entity = new HttpEntity<String>(getHeader());

        ResponseEntity<String> result2 = restTemplate.exchange(baseURL + url,
                HttpMethod.GET, entity, String.class);

        ResponseEntity<Webhook[]> result = restTemplate.exchange(baseURL + url,
                HttpMethod.GET, entity, Webhook[].class);
        return result.getBody();
    }

    public void deleteWebhook(Webhook [] webHookId) {
        for (int i = 0; i < webHookId.length ; i++) {
            String url = "/hooks/outgoing/" + webHookId[i].getId();
            HttpEntity<String> entity = new HttpEntity<String>(getHeader());
            ResponseEntity<String> result = restTemplate.exchange(baseURL + url, HttpMethod.DELETE,
                    entity, String.class);
        }
    }

    public String login(String username, String password) {
        String url = "/users/login";
        String requestJson = "{\"login_id\":\"" + username + "\",\"password\":\"" + password + "\"}";

        HttpEntity<String> entity = new HttpEntity<String>(requestJson);
        ResponseEntity<String> result = restTemplate.exchange(baseURL + url, HttpMethod.POST,
                entity, String.class);

        List<String> cookie = (List<String>) result.getHeaders().get("Set-Cookie");

        if (cookie != null && cookie.size() > 0) {
            String str = cookie.get(0);
            String[] chunks = str.split(";");
            if (chunks != null && chunks.length > 0) {
                String token = chunks[0];
                String auth = token.split("=")[1];
                return auth;
            }

        }

        return null;
    }

    public Id getUserId(String name) {
        String userURL = "/users/usernames";
        String requestJson = "[\"" + name + "\"]";
        HttpEntity<String> entity = new HttpEntity<String>(requestJson, getHeader());
        Id[] id = restTemplate.postForObject(baseURL + userURL, entity, Id[].class);

        return id[0];

    }

    public Id getTeam(String name) {
        String url = "/teams/name/" + name;
        return getId(url);
    }

    public Id getChannel(String teamId, String channelName) {
        String url = "/teams/" + teamId + "/channels/name/" + channelName;
        return getId(url);
    }

    public Id getId(String url) {
        HttpEntity<String> entity = new HttpEntity<String>(getHeader());
        ResponseEntity<Id> result = restTemplate.exchange(baseURL + url, HttpMethod.GET, entity, Id.class);
        return result.getBody();
    }

    public Id postMessage(String channelId, String message) {
        String url = "/posts";

//        String requestJson = "{\"channel_id\": \"" + channelId + "\"," +
//                "\"message\": \"" + message + "\"}";
        ResponseMessage msg = new ResponseMessage();
        msg.setChannel_id(channelId);
        msg.setMessage(message);

        HttpEntity<ResponseMessage> entity = new HttpEntity<>(msg, getHeader());
        Id result = restTemplate.postForObject(baseURL + url, entity, Id.class);

        return result;
    }

    public Posts getPosts(String channelId, String after) {
        String url = "/channels/" + channelId + "/posts";

        ResponseEntity<Posts> result = null;

        String afterParam = "";
        if (after != null) {
            afterParam += "?after=" + after;
        }
        HttpEntity<String> entity = new HttpEntity<String>(getHeader());
        result = restTemplate.exchange(baseURL + url + afterParam, HttpMethod.GET, entity, Posts.class);

        return result.getBody();

    }

    public Post getPost(String postId) {
        String url = "/posts/" + postId;
        HttpEntity<String> entity = new HttpEntity<String>(getHeader());
        ResponseEntity<Post> result = restTemplate.exchange(baseURL + url, HttpMethod.GET, entity, Post.class);
        return result.getBody();
    }

    public void setToken(String token) {
        this.token = token;

    }
}
