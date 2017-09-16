package com.chatbot.amazon;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.io.*;

public class S3Store {
    public static final String ZIP = ".zip";
    public static final String TMP = "/tmp/";
    private static String bucketName = "aws-codestar-us-west-2-894794566272";


    public static void main(String[] args) throws IOException {
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_WEST_2)
                .withCredentials(new AWSStaticCredentialsProvider(EC2.credentials()))
                .build();
        try {
            String key = "acs";
            System.out.println("Downloading an object");
            S3Object s3object = s3Client.getObject(new GetObjectRequest(

                    bucketName, key));
         //   System.out.println("Content-Type: "  +
                  //  s3object.getObjectMetadata().getContentType());

            String etag = s3object.getObjectMetadata().getETag();
           // displayTextInputStream(s3object.getObjectContent());

            S3ObjectInputStream input = s3object.getObjectContent();
            FileOutputStream fos = new FileOutputStream(new File("test.zip"));
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = input.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
            }
            input.close();
            fos.close();


            // Get a range of bytes from an object.

//            GetObjectRequest rangeObjectRequest = new GetObjectRequest(
//                    bucketName, key);
//            rangeObjectRequest.setRange(0, 10);
//            S3Object objectPortion = s3Client.getObject(rangeObjectRequest);
//
//            System.out.println("Printing bytes retrieved.");
//            displayTextInputStream(objectPortion.getObjectContent());

        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which" +
                    " means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means"+
                    " the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }

    public static String download(String appName) {
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_WEST_2)
                .withCredentials(new AWSStaticCredentialsProvider(EC2.credentials()))
                .build();
        try {

            System.out.println("Downloading an object");
            S3Object s3object = s3Client.getObject(new GetObjectRequest(
                    bucketName, appName));

            S3ObjectInputStream input = s3object.getObjectContent();
            FileOutputStream fos = new FileOutputStream(new File(TMP + appName + ZIP));
            long size = s3object.getObjectMetadata().getContentLength();
            long done = 0;
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = input.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
                //done+=read_len;

               // System.out.println(size-done);

            }
            input.close();
            fos.close();
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            return  e.getErrorMessage();
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            return e.getMessage();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return e.getMessage();
        } finally {
            s3Client.shutdown();
            return "Downloaded " +TMP +appName + "-zip";
        }


    }

    public static String getTag(String name) {
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_WEST_2)
                .withCredentials(new AWSStaticCredentialsProvider(EC2.credentials()))
                .build();
            System.out.println("Downloading an object");
            S3Object s3object = s3Client.getObject(new GetObjectRequest(
                    bucketName, name));

            String etag = s3object.getObjectMetadata().getETag();

            s3Client.shutdown();
            return etag;
    }

    private static void displayTextInputStream(InputStream input)
            throws IOException {
        // Read one text line at a time and display.
        BufferedReader reader = new BufferedReader(new
                InputStreamReader(input));
        while (true) {
            String line = reader.readLine();
            if (line == null) break;

            System.out.println("    " + line);
        }
        System.out.println();
    }

}
