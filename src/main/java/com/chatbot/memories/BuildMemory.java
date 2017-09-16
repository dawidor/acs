package com.chatbot.memories;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.codebuild.AWSCodeBuild;
import com.amazonaws.services.codebuild.model.CreateProjectResult;
import com.amazonaws.services.codebuild.model.StartBuildRequest;
import com.amazonaws.services.codebuild.model.StartBuildResult;
import com.chatbot.MemoryConstants;
import com.chatbot.amazon.Build;
import com.chatbot.amazon.EC2;
import com.chatbot.models.Message;
import jdk.nashorn.internal.ir.debug.JSONWriter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.Buffer;
import java.util.List;

public class BuildMemory implements Memory {

    public static final String BUILD = "build ";
    public static final String REPOS = "repo=";
    public static final String NAMES = "name=";
    public static final String START = "start";
    public static final String TYPE = "type=";

    @Override
    public Message getAnswear(Message message) {
        Message result = new Message();

        String words = message.getText().toLowerCase();
        List<String> repos = Memory.getValue(words, REPOS);
        List<String> projectNames = Memory.getValue(words, NAMES);
        List<String> types = Memory.getValue(words, TYPE);
        if (projectNames.size()>0) {
            if (words.contains(MemoryConstants.BOT + BUILD + NAMES) && repos.size()>0) {

                String projectName = projectNames.get(0).replaceAll(NAMES, "");
                //String repoLocation = "https://git-codecommit.us-west-2.amazonaws.com/v1/repos/acs";

                String repoLocation = repos.get(0).replaceAll(REPOS, "");
                BasicAWSCredentials credentials = EC2.credentials();
                AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);

                AWSCodeBuild codeBuild = Build.connect(provider);

                String type = null;
                if (types!=null && types.size()>0) {
                    type = types.get(0).replaceAll(TYPE, "");
                }

                CreateProjectResult buildResult = Build.createProject(codeBuild, projectName, repoLocation, type);
                String text = "Project " + projectName + " created..." + buildResult.getProject().toString();
                if (type.equalsIgnoreCase("github")
                        || type.equalsIgnoreCase("bitbucket")) {
                    result.setText(text);
                } else {
                    result.setText(text.toString());
                }

                codeBuild.shutdown();
            } else if (words.contains(MemoryConstants.BOT + BUILD + START)) {
                String projectName = projectNames.get(0).replaceAll(NAMES, "");

                BasicAWSCredentials credentials = EC2.credentials();
                AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);


                AWSCodeBuild codeBuild = Build.connect(provider);
                StartBuildRequest build = getBuild(provider, projectName, null);
                StartBuildResult startBuildResult = codeBuild.startBuild(build);

               // + startBuildResult.getBuild().toString()
                result.setText("Build " + projectName + " created..." +
                        startBuildResult.getBuild().toString());
                codeBuild.shutdown();
            } else {

                result.setText("Sorry, not reconized command...");
            }
        } else {
            result.setText("Sorry, not reconized command...");
        }


        return result;
    }

    private StartBuildRequest getBuild (AWSStaticCredentialsProvider provider,
                                        String projectName,
                                        String repoLocation) {



        StartBuildRequest build = new StartBuildRequest();
        build.setProjectName(projectName);
        build.setRequestCredentialsProvider(provider);
//        ProjectArtifacts artifactsOverride = new ProjectArtifacts();
//        artifactsOverride.setType(ArtifactsType.S3);
//        artifactsOverride.setLocation("arn:aws:s3:::aws-codestar-us-west-2-894794566272");
//        artifactsOverride.setPackaging(ArtifactPackaging.ZIP);
//        artifactsOverride.setNamespaceType(ArtifactNamespace.BUILD_ID);
//        artifactsOverride.setName("acs");

        //build.setSourceVersion();

        //artifactsOverride.

       // build.setArtifactsOverride(artifactsOverride);

        return build;
    }


}
