package com.chatbot.memories;

import com.chatbot.MemoryConstants;
import com.chatbot.amazon.Deploy;
import com.chatbot.models.Message;

import java.util.List;


public class DeployMemory implements Memory {

    public static final String DEPLOY = "deploy";
    public static final String SPACE = " ";

    public static final String CREATE = "create";
    public static final String RUN = "run";
    public static final String DEPLOYMENT_GROUP = "DeploymentGroup";

    @Override
    public Message getAnswear(Message message) {
        Message result = new Message();

        String words = message.getText().toLowerCase();


        if (words.contains(MemoryConstants.BOT + DEPLOY)) {

            String [] wordsArray = Memory.getValue(words);
            String applicationName = wordsArray[3];

            //String instanceName = "acs-WebApp";

            if (words.contains(CREATE) && wordsArray.length == 5) {
                String instanceKey = wordsArray[4];
                Deploy.createApplication(applicationName);
                Deploy.createDeploymentGroup(applicationName,
                        applicationName + DEPLOYMENT_GROUP,
                        instanceKey
                        );
                result.setText("Application to deploy created. You can run deployment now...");
            } else if (words.contains(RUN) && wordsArray.length==5) {
                List<String> etagList = Memory.getValue(words, "etag=");
                String etag = null;
                if (etagList!=null && etagList.size()>0) {
                    etag = etagList.get(0).replaceAll("etag=", "");
                }
                Deploy.createDeployment(applicationName, applicationName + DEPLOYMENT_GROUP, etag);
                result.setText("Deployment for " + applicationName + " started...");
            }

        } else {
            result.setText("Unrecognized command... Did you mean: bot deploy create/run applicationName?");
        }


        return result;

    }
}
