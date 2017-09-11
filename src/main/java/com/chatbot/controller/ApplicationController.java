package com.chatbot.controller;

import com.chatbot.Response;
import com.chatbot.memories.Memory;
import com.chatbot.models.Id;
import com.chatbot.models.Message;
import com.chatbot.models.Webhook;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/")
public class ApplicationController {

    private static final String MESSAGE_FORMAT = "Hello %s!";


    private static final Logger log = Logger.getLogger(ApplicationController.class);

    private MattermostService mattermostService;
    private Memory botMemory = null;

    public ApplicationController() {
        botMemory = new BotMemory();
        mattermostService = new MattermostService();

        String token = mattermostService.login("dr4aa", "lolo4547");
        mattermostService.setToken(token);
    }

    @RequestMapping(method = RequestMethod.GET, path = "reset/webhook")
    public Response resetWebhook() {

        Id user = mattermostService.getUserId("dr4aa");
        Id teamId = mattermostService.getTeam("pret");
        Id channelId = mattermostService.getChannel(teamId.getId(), "chatbot");

        deleteWebhooksOut(teamId, channelId);

        List <String> words = new ArrayList<String>();
        words.add("hi");
        words.add("how");
        words.add("time");
        words.add("ec2");
        words.add("bot");
        words.add("Bot");
        words.add("BOT");
        words.add("help");



        mattermostService.createWebhook(teamId.getId(), channelId.getId(),
                "/callback/message/",
                words);
        System.out.println(user);

        return new Response("Done");
    }

    private void deleteWebhooksOut(Id teamId, Id channelId) {
        Webhook [] webhookIds = mattermostService.listWebhook(teamId.getId(), channelId.getId());
        if (webhookIds!=null) {
            mattermostService.deleteWebhook(webhookIds);
        }
    }

    private void getCurrentPosts() {
 //       Posts postsIds = mattermostService.getPosts(channelId.getId(), null);
//        int lastId = postsIds.getOrder().length;
//
//        String after = postsIds.getOrder()[lastId];
//
//        Posts postsIdsAfter = mattermostService.getPosts(channelId.getId(), after);
//
//        for (int i=0; i<postsIdsAfter.getOrder().length-1; i++) {
//            String postId = postsIdsAfter.getOrder()[i];
//            Post post = mattermostService.getPost(postId);
//            log.info(post.getUser_id() + ": " + post.getMessage());
//        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public Response callback(@RequestBody Message message) {

        Message resp = botMemory.getAnswear(message);
        mattermostService.postMessage(message.getChannel_id(), resp.getText());

        return new Response("Got message: " + message.getUser_name()
                + " : " + message.getPost_id() + " : " + message.getText());
    }


}