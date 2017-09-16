package com.chatbot.amazon;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.codebuild.AWSCodeBuild;
import com.amazonaws.services.codebuild.model.*;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.s3.AmazonS3Client;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class EC2
{
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

        String ami_id = "ami-6df1e514";

        String instance_id = "i-009b61df59b60ed2c";

        BasicAWSCredentials credentials = credentials();
        AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);
//        AmazonEC2ClientBuilder.standard()
//                .withCredentials(new AWSStaticCredentialsProvider(credentials()))
//                .build();


        //AmazonEC2Client c;



        AWSCodeBuild codeBuild = Build.connect(provider);

        BatchGetProjectsRequest r = new BatchGetProjectsRequest();
        r.withNames("lalala");
        BatchGetProjectsResult res  = codeBuild.batchGetProjects(r);


ListBuildsRequest requestBuilds = new ListBuildsRequest();


        String projectName = "ChatBot";
//        String location = "https://git-codecommit.us-west-2.amazonaws.com/v1/repos/acs\"";
//
//            StartBuildRequest build = new StartBuildRequest();
//        build.setProjectName("ChatBot");
//        build.setRequestCredentialsProvider(provider);
//        ProjectArtifacts artifactsOverride = new ProjectArtifacts();
//        artifactsOverride.setType(ArtifactsType.CODEPIPELINE);
//        artifactsOverride.setLocation(location);
//        //artifactsOverride.
//
//        build.setArtifactsOverride(artifactsOverride);
//
//        StartBuildResult startBuildResult = codeBuild.startBuild(build);
//
//
//
//        //Build.createProject(codeBuild, projectName, location);
//


        codeBuild.shutdown();

        boolean z = false;
    }



    public static void shutdown(AmazonEC2 ec2) {
            ec2.shutdown();
    }


    public static BasicAWSCredentials credentials() {
        String accessKey = System.getProperty("ACCESS_KEY");
        String secretKey = System.getProperty("SECRET_KEY");
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey,
                secretKey);
        return  awsCreds;
    }
    public static AmazonEC2 connect() {
        BasicAWSCredentials awsCreds = credentials();


        AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard()
                .withRegion(Regions.US_WEST_2)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))

                .build();
        return ec2;
    }

    public static String runI(AmazonEC2 ec2, String ami, String key, String name) {

        RunInstancesRequest runInstancesRequest =
                new RunInstancesRequest();

        IamInstanceProfileSpecification profile = new IamInstanceProfileSpecification();
        profile.setArn("arn:aws:iam::894794566272:instance-profile/InstanceRoleS3");
        //profile.setName("CodeStarWorker-acs-WebApp");

       // wget https://aws-codedeploy-us-west-2.s3.amazonaws.com/latest/install


        //aws-codedeploy-us-east-2


        TagSpecification tagSpec = new TagSpecification();
        tagSpec.setResourceType(ResourceType.Instance);

        Tag tag = new Tag ();
        tag.setKey("Name");
        tag.setValue(name);
        Collection <Tag> coll = Arrays.asList(tag);
        tagSpec.setTags(coll);


        String userdata = "#!/bin/bash -ex\n" +
                "wget -O /usr/local/bin/get_authorized_keys https://s3-us-west-2.amazonaws.com/awscodestar-remote-access-us-west-2/get_authorized_keys\n" +
                "chmod 755 /usr/local/bin/get_authorized_keys\n" +
                "sed -i '/AuthorizedKeysCommand /s/.*/AuthorizedKeysCommand \\/usr\\/local\\/bin\\/get_authorized_keys/g' /etc/ssh/sshd_config\n" +
                "sed -i '/AuthorizedKeysCommandUser /s/.*/AuthorizedKeysCommandUser root/g' /etc/ssh/sshd_config\n" +
                "/etc/init.d/sshd restart\n" +
                "yum update -y aws-cfn-bootstrap\n" +
                "yum install -y aws-cli\n" +
                "# Install the AWS CodeDeploy Agent.\n" +
                "cd /home/ec2-user/\n" +
                "wget https://aws-codedeploy-us-west-2.s3.amazonaws.com/latest/codedeploy-agent.noarch.rpm\n" +
                "yum -y install codedeploy-agent.noarch.rpm\n" +
                "# Install pip and python dev libraries.\n" +
                "yum install -y python27-devel python27-pip gcc\n" +
                "pip install boto3\n" +
                "pip install pycrypto\n" +
                "yum install -y java-1.8.0-openjdk-devel\n" +
                "yum remove -y java-1.7.0-openjdk\n" +
                "wget https://s3.amazonaws.com/dhqs-mirror-iad/apache-tomcat-8.0.39.tar.gz\n" +
                "mkdir /opt/tomcat\n" +
                "tar xvf apache-tomcat-8*tar.gz -C /opt/tomcat --strip-components=1\n" +
                "cd /opt/tomcat\n" +
                "sed -i 's|port=\"8080\"|port=\"80\"|g' conf/server.xml\n" +
                "rm -rf webapps/ROOT\n" +
                "chgrp ec2-user webapps/\n" +
                "chmod 774   webapps/\n" +
                "bin/startup.sh";

        BASE64Encoder encoder = new BASE64Encoder();
        userdata = encoder.encode(userdata.getBytes());

        runInstancesRequest
                .withImageId(ami)
                .withSecurityGroups("WebServerSG")
                .withTagSpecifications(tagSpec)
                .withInstanceType("t2.micro")
                .withMinCount(1)
                .withMaxCount(1)
                .withIamInstanceProfile(profile)
                .withKeyName(key)
                .withUserData(userdata);


        //.withSecurityGroups("my-security-group");
        RunInstancesResult result = ec2.runInstances(
                runInstancesRequest);

        String instance_id = result.getReservation().getReservationId();
        return instance_id;
    }

    public static StartInstancesResult startI (final  AmazonEC2 ec2, String instance_id) {
        StartInstancesRequest request = new StartInstancesRequest()
                .withInstanceIds(instance_id);




        StartInstancesResult result = ec2.startInstances(request);
        return result;
    }

    public static StopInstancesResult stopI(final  AmazonEC2 ec2, String instance_id) {
        StopInstancesRequest request = new StopInstancesRequest()
                .withInstanceIds(instance_id);

        StopInstancesResult result = ec2.stopInstances(request);
        return result;
    }

    public static RebootInstancesResult rebooting(final  AmazonEC2 ec2, String instance_id) {
        RebootInstancesRequest request = new RebootInstancesRequest()
                .withInstanceIds(instance_id);

        RebootInstancesResult response = ec2.rebootInstances(request);
        return response;
    }

    public static String describe(AmazonEC2 ec2) {
        boolean done = false;

        StringBuffer sb = new StringBuffer();

        while(!done) {
            DescribeInstancesRequest request = new DescribeInstancesRequest();
            DescribeInstancesResult response = ec2.describeInstances(request);

            for(Reservation reservation : response.getReservations()) {
                for(Instance instance : reservation.getInstances()) {


                    sb.append("Id: ").append(instance.getInstanceId()).append(" \n ")
                            .append("Name: ").append(instance.getKeyName()).append(" \n ")
                            .append("AMI: ").append(instance.getImageId()).append(" \n ")
                            .append("Type: ").append(instance.getVirtualizationType()).append(" \n ")
                            .append("State: ").append(instance.getState().getName()).append(" \n ")
                            .append("IAM: ").append(instance.getIamInstanceProfile()!=null ? instance.getIamInstanceProfile().getId() : "").append(" \n ")
                            .append(instance.getIamInstanceProfile()!=null ? instance.getIamInstanceProfile().getArn() : "").append(" \n ");

                    instance.getTags().forEach(tag -> {
                        sb.append("Tag-name: " + tag.getKey()).append(" value: " + tag.getValue()).append(" \n ");
                    });
                    sb.append("\n");
                }
            }

            request.setNextToken(response.getNextToken());

            if(response.getNextToken() == null) {
                done = true;
            }
        }
        return sb.toString();
    }

    public static void disassociateIam(AmazonEC2 ec2, String assId) {
        DisassociateIamInstanceProfileRequest dp = new DisassociateIamInstanceProfileRequest();

       dp.setAssociationId(assId.toUpperCase());



        ec2.disassociateIamInstanceProfile(dp);
    }

    public static void setIamProfile(AmazonEC2 ec2, String instanceId) {

        IamInstanceProfileSpecification profileSpec = new IamInstanceProfileSpecification();
       //profileSpec.setArn("arn:aws:iam::894794566272:instance-profile/awscodestar-acs-WebAppInstanceProfile-PXO9JYELWVOS");
        //profileSpec.setArn("arn:aws:iam::894794566272:instance-profile/InstanceRoleS3");
        profileSpec.setName("InstanceRoleS3");
//profileSpec.setName("CodeStarWorker-acs-WebApp");
        AssociateIamInstanceProfileRequest profile = new AssociateIamInstanceProfileRequest();
        profile.setIamInstanceProfile(profileSpec);
        profile.setInstanceId(instanceId);
        AssociateIamInstanceProfileResult result = ec2.associateIamInstanceProfile(profile);

        boolean c= false;
    }
    public static String getIp(AmazonEC2 ec2, String instanceId) {
       Instance instance = findInstance(ec2, instanceId);

       String ip = instance.getPublicIpAddress();
       return ip;
    }

    public static Instance findInstance(AmazonEC2 ec2, String instance_id) {
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        DescribeInstancesResult response = ec2.describeInstances(request);

        for(Reservation reservation : response.getReservations()) {
            for (Instance instance : reservation.getInstances()) {
                if (instance.getInstanceId().equals(instance_id)) {

                    return instance;

                }


            }
        }
        return null;
    }

    public static void startMonitor (AmazonEC2 ec2, String instance_id) {

        MonitorInstancesRequest request = new MonitorInstancesRequest()
                .withInstanceIds(instance_id);

        ec2.monitorInstances(request);
    }

    public static void stopMonitor(AmazonEC2 ec2, String instance_id) {

        UnmonitorInstancesRequest request = new UnmonitorInstancesRequest()
                .withInstanceIds(instance_id);

        ec2.unmonitorInstances(request);

    }


}
