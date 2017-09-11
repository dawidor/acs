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
                                             String location) {
        CreateProjectRequest projectRequest = new CreateProjectRequest();
        projectRequest.setName(projectName);

        projectRequest.setServiceRole("arn:aws:iam::894794566272:role/CodeStarWorker-acs-CodeBuild");

        ProjectArtifacts artifact = getArtifact(projectName);

        projectRequest.setArtifacts(artifact);

        projectRequest.setSource(getProjectSource(location));

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

    private static ProjectSource getProjectSource(String repoLocation) {
        ProjectSource ps = new ProjectSource();
        ps.setLocation(repoLocation);
        ps.setType(SourceType.CODECOMMIT);
        ps.setAuth(getAuth());
        ps.setType(SourceType.CODECOMMIT);
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

    //s3://aws-codestar-us-west-2-894794566272/acs/target/ROOT.war
}

 //d-FU7MWYGCO

        // aws deploy list-deployment-instances --deployment-id d-FU7MWYGCO --instance-status-filter Succeeded