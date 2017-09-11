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

import java.util.List;

public class BuildMemory implements Memory {

    public static final String BUILD = "build ";
    public static final String REPOS = "repo=";
    public static final String NAMES = "name=";
    public static final String START = "start";

    @Override
    public Message getAnswear(Message message) {
        Message result = new Message();

        String words = message.getText().toLowerCase();
        List<String> repos = Memory.getValue(words, REPOS);
        List<String> projectNames = Memory.getValue(words, NAMES);
        if (projectNames.size()>0) {
            if (words.contains(MemoryConstants.BOT + BUILD + NAMES) && repos.size()>0) {

                String projectName = projectNames.get(0).replaceAll(NAMES, "");
                //String repoLocation = "https://git-codecommit.us-west-2.amazonaws.com/v1/repos/acs";

                String repoLocation = repos.get(0).replaceAll(REPOS, "");
                BasicAWSCredentials credentials = EC2.credentials();
                AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);


                AWSCodeBuild codeBuild = Build.connect(provider);

                CreateProjectResult buildResult = Build.createProject(codeBuild, projectName, repoLocation);
                result.setText("Project " + projectName + " created..." + buildResult.getProject().toString());
                codeBuild.shutdown();
            } else if (words.contains(MemoryConstants.BOT + BUILD + START)) {
                String projectName = projectNames.get(0).replaceAll(NAMES, "");

                //String repoLocation = repos.get(0).replaceAll(REPOS, "");

                BasicAWSCredentials credentials = EC2.credentials();
                AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);


                AWSCodeBuild codeBuild = Build.connect(provider);
                StartBuildRequest build = getBuild(provider, projectName, null);
                StartBuildResult startBuildResult = codeBuild.startBuild(build);

                result.setText("Build " + projectName + " created..." + startBuildResult.getBuild().toString());
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
