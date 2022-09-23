package com.cc.app.utils;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;
import com.cc.app.properties.AppTierProperties;
import io.vavr.control.Try;
import lombok.extern.java.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Log
public class AWSUtils {

    private final AmazonSQS amazonSQS;

    private final AmazonS3 amazonS3;

    private final AppTierProperties appTierProperties;

    public AWSUtils(AppTierProperties appTierProperties) {
        this.amazonSQS = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(appTierProperties.awsCredentials()))
                .withRegion(Regions.US_EAST_1)
                .build();
        this.amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(appTierProperties.awsCredentials()))
                .withRegion(Regions.US_EAST_1)
                .build();
        this.appTierProperties = appTierProperties;
    }

    public void sendMessageToSQS(String message) {
        Try.run(() -> {
            amazonSQS.sendMessage(appTierProperties.getRESPONSE_SQS(), message);
            log.info("Message Sent to Response SQS" + message);
        }).onFailure(ex -> log.severe(ex.getMessage()));
    }

    public void deleteMessageFromSQS(Message message) {
        Try.run(() -> {
            var receiptHandle = message.getReceiptHandle();
            var deleteMessageRequest = new DeleteMessageRequest()
                    .withQueueUrl(appTierProperties.getREQUEST_SQS())
                    .withReceiptHandle(receiptHandle);
            amazonSQS.deleteMessage(deleteMessageRequest);
            log.info("Deleted Message from SQS: " + message.getBody());
        }).onFailure(ex -> log.severe(ex.getMessage()));
    }

    public Message readMessageFromSQS() {
        return Try.of(() -> {
                    var receiveMessageRequest = new ReceiveMessageRequest()
                            .withQueueUrl(appTierProperties.getREQUEST_SQS())
                            .withMaxNumberOfMessages(1)
                            .withWaitTimeSeconds(15);

                    var result = amazonSQS.receiveMessage(receiveMessageRequest);
                    List<Message> messages = result.getMessages();

                    if (messages == null || messages.isEmpty()) {
                        log.severe("No messages present in the SQS, Terminating this App Tier EC2 Instance");
                        amazonSQS.shutdown();
                        amazonS3.shutdown();
                        terminateEC2();
                    }

                    assert messages != null;
                    log.info("Number of Messages Read from queue: " + messages.size());
                    var message = messages.get(0);
                    log.info("Read Message from SQS: " + message.getBody());
                    return message;
                })
                .onFailure(ex -> log.severe(ex.getMessage()))
                .getOrElse(new Message());
    }

    public void downloadFromS3(String imageName) throws IOException {
        Try.run(() -> {
            S3Object s3Obj = amazonS3.getObject(appTierProperties.getREQUEST_S3(), imageName);
            S3ObjectInputStream s3InputStream = s3Obj.getObjectContent();
            Path outputPath = Paths.get("/home/ubuntu/classifier/" + imageName);
            long imageSize = Files.copy(s3InputStream, outputPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Downloaded Image from S3: " + imageName + " , with a Size: " + imageSize);
        }).onFailure(ex -> log.severe(ex.getMessage())).get();
    }

    public void saveInS3(String imageName, String result) {
        Try.run(() -> {
            amazonS3.putObject(appTierProperties.getRESPONSE_S3(), imageName, result);
            log.info("Saved Object in S3: " + result);
        }).onFailure(ex -> log.severe(ex.getMessage()));
    }

    public void terminateEC2() {
        Try.run(() -> {
            String cmd = "sudo shutdown -h now";
            var runtime = Runtime.getRuntime();
            runtime.exec(cmd);
            System.exit(0);
        }).onFailure(ex -> log.severe(ex.getMessage()));
    }
}
