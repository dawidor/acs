package com.chatbot.amazon;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.codebuild.AWSCodeBuild;
import com.amazonaws.services.codebuild.AWSCodeBuildClientBuilder;
import com.amazonaws.services.codebuild.model.*;

public class Build {

    public static AWSCodeBuild connect(AWSCredentialsProvider provider) {

        //ClientConfiguration config;

        AWSCodeBuild result = AWSCodeBuildClientBuilder
                .standard()
                .withRegion(Regions.US_WEST_2)
                .withCredentials(provider)
          //      .withClientConfiguration(config)
                .build();
        return result;
    }

    public static CreateProjectResult createProject(AWSCodeBuild codeBuild,
                                             String projectName,
                                             String location, String typeStr) {
        CreateProjectRequest projectRequest = new CreateProjectRequest();
        projectRequest.setName(projectName);

        projectRequest.setServiceRole("arn:aws:iam::894794566272:role/CodeStarWorker-acs-CodeBuild");

        ProjectArtifacts artifact = getArtifact(projectName);

        projectRequest.setArtifacts(artifact);

        SourceType type = null;
        if ("bitbucket".equalsIgnoreCase(typeStr)) {
            type = SourceType.BITBUCKET;
        } else if ("github".equalsIgnoreCase(typeStr)) {
            type = SourceType.GITHUB;
        } else {
            type = SourceType.CODECOMMIT;
        }

        projectRequest.setSource(getProjectSource(location, type));

        projectRequest.setEnvironment(getEnvironment());

        CreateProjectResult result = codeBuild.createProject(projectRequest);

        return result;
    }

    private static ProjectArtifacts getArtifact(String artifactName) {
        ProjectArtifacts artifact = new ProjectArtifacts();
        artifact.setName(artifactName);
        artifact.setType(ArtifactsType.S3);
        artifact.setLocation("arn:aws:s3:::aws-codestar-us-west-2-894794566272");
        artifact.setNamespaceType(ArtifactNamespace.NONE);
        //artifact.setNamespaceType(ArtifactNamespace.BUILD_ID);
        artifact.setPackaging(ArtifactPackaging.ZIP);
        return artifact;
    }

    private static ProjectSource getProjectSource(String repoLocation, SourceType type) {
        ProjectSource ps = new ProjectSource();
        ps.setLocation(repoLocation);
        ps.setType(type);
        ps.setAuth(getAuth());
        ps.setBuildspec("version: 0.2\n" +
                "\n" +
                "phases:\n" +
                "  build:\n" +
                "    commands:\n" +
                "      - mvn package\n" +
                "\n" +
                "artifacts:\n" +
                "  files:\n" +
                "    - target/*.war\n");

        return ps;
    }

    private static SourceAuth getAuth() {
        SourceAuth auth = new SourceAuth();
        auth.setType(SourceAuthType.OAUTH);


        return auth;
    }

    private static ProjectEnvironment getEnvironment() {
        ProjectEnvironment env = new ProjectEnvironment();
        env.setComputeType(ComputeType.BUILD_GENERAL1_SMALL);
        env.setType(EnvironmentType.LINUX_CONTAINER);

        env.setImage("aws/codebuild/java:openjdk-8");
        return env;
    }


}

