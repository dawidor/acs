package com.chatbot.amazon;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.codedeploy.AmazonCodeDeploy;
import com.amazonaws.services.codedeploy.AmazonCodeDeployClientBuilder;
import com.amazonaws.services.codedeploy.model.*;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Deploy {
    public static AmazonCodeDeploy connect(AWSCredentialsProvider provider) {

        //ClientConfiguration config;

        AmazonCodeDeploy result = AmazonCodeDeployClientBuilder
                .standard()
                .withRegion(Regions.US_WEST_2)
                .withCredentials(provider)
                //      .withClientConfiguration(config)
                .build();
        return result;
    }

    public static BasicAWSCredentials credentials() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIAI5RITG7P537CKOXA",
                "GuVYLtPwm3EFOYpswlOQFvzCHilKIUA+yg+RfwL/");
        return  awsCreds;
    }

    public static CreateApplicationResult createApplication(String applicationName) {

        CreateApplicationRequest request = new CreateApplicationRequest();


        AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials());

        AmazonCodeDeploy deploy = Deploy.connect(provider);

        request.setApplicationName(applicationName);
        request.setRequestCredentialsProvider(provider);


         CreateApplicationResult result = deploy.createApplication(request);

         deploy.shutdown();

         return result;
    }

    private static EC2TagSet getEc2TagSet(String environmentKey) {
        EC2TagSet ec2Tag = new EC2TagSet();

        Collection<List<EC2TagFilter>> ec2TagSetList = new ArrayList<>();
        List<EC2TagFilter> list =new ArrayList<>();
        EC2TagFilter filler = new EC2TagFilter();
        filler.setKey("Name");
        filler.setType(EC2TagFilterType.KEY_AND_VALUE);
        filler.setValue(environmentKey);
        list.add(filler);
        ec2TagSetList.add(list);

        ec2Tag.setEc2TagSetList(ec2TagSetList);
        return ec2Tag;
    }


    public static CreateDeploymentGroupResult createDeploymentGroup(String applicationName,
                                                                    String deploymentGroupName,
                                                                    String instanceKey) {
        CreateDeploymentGroupRequest deployGroupRequest = new CreateDeploymentGroupRequest();

        deployGroupRequest.setApplicationName(applicationName);
        deployGroupRequest.setDeploymentGroupName(deploymentGroupName);

        deployGroupRequest.setEc2TagSet(getEc2TagSet(instanceKey));
       // deployGroupRequest.setServiceRoleArn("arn:aws:iam::894794566272:role/CodeStarWorker-acs-CodeDeploy");

        deployGroupRequest.setServiceRoleArn("arn:aws:iam::894794566272:role/ServiceDeploy");
        AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials());
        AmazonCodeDeploy deploy = Deploy.connect(provider);
        CreateDeploymentGroupResult result = deploy.createDeploymentGroup(deployGroupRequest);

        deploy.shutdown();
        return result;
    }

    public static CreateDeploymentResult createDeployment(String applicationName,
                                                          String deploymentGroupName,
                                                          String etag) {
        CreateDeploymentRequest deployRequest = new CreateDeploymentRequest();

        deployRequest.setApplicationName(applicationName);
        deployRequest.setDeploymentGroupName(deploymentGroupName);

        RevisionLocation revision = new RevisionLocation();
        revision.setRevisionType(RevisionLocationType.S3);
        S3Location loc = new S3Location();
        loc.setBucket("aws-codestar-us-west-2-894794566272");
        loc.setKey(applicationName);
        loc.setETag(etag);
        loc.setBundleType(BundleType.Zip);
        revision.setS3Location(loc);
        deployRequest.setRevision(revision);

        AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials());
        AmazonCodeDeploy deploy = Deploy.connect(provider);
        CreateDeploymentResult result = deploy.createDeployment(deployRequest);

        deploy.shutdown();
        return result;
    }

    public static void main(String...args) {
        System.out.println("Hi");

        try {
            System.setProperty("log4j.configuration",
                    new File("/development/amazonclient/src/main/resources/log4j.properties")
                            .toURI().toURL().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        java.security.Security.setProperty("networkaddress.cache.ttl" , "60");

        String appName = "chatbot12";

        String deploymentGroupName = "chatbot13";

        Deploy.createApplication(appName);

        Deploy.createDeploymentGroup(appName, deploymentGroupName,"acs-WebApp");

        Deploy.createDeployment(appName, deploymentGroupName, null);

        boolean z = false;
    }

}


