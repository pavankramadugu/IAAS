package com.cc.webtier.helper.sqs;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.cc.webtier.properties.WebTierProperties;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class SQSHelper {
    private final WebTierProperties webTierProperties;

    public SQSHelper(WebTierProperties webTierProperties) {
        this.webTierProperties = webTierProperties;
    }

    public Optional<String> getSQSMessageCount() {
        var attributesRequest = new GetQueueAttributesRequest()
                .withQueueUrl(webTierProperties.getRequestQueue())
                .withAttributeNames("ApproximateNumberOfMessages");

        return Try.of(() -> {
            var amazonSQS = AmazonSQSClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(webTierProperties.getAwsCredentials()))
                    .withRegion(Regions.US_EAST_1)
                    .build();
                    var attributes = amazonSQS.getQueueAttributes(attributesRequest)
                            .getAttributes();
                    log.info("Getting Message Count from Request Queue");
                    amazonSQS.shutdown();
                    return Optional.of(attributes.get("ApproximateNumberOfMessages"));
                }).onFailure(ex -> log.error(ex.getMessage()))
                .get();
    }

    public void publishMessage(String message) {
        Try.run(() -> {
                    var amazonSQS = AmazonSQSClientBuilder.standard()
                            .withCredentials(new AWSStaticCredentialsProvider(webTierProperties.getAwsCredentials()))
                            .withRegion(Regions.US_EAST_1)
                            .build();
                    amazonSQS.sendMessage(webTierProperties.getRequestQueue(), message);
                    log.info("Sent Image details to Queue");
                    amazonSQS.shutdown();
                })
                .onFailure(ex -> log.error(ex.getMessage()));
    }

    public List<Message> readMessagesFromSQS() {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest()
                .withQueueUrl(webTierProperties.getResponseQueue())
                .withMaxNumberOfMessages(10)
                .withWaitTimeSeconds(15);
        var amazonSQS = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(webTierProperties.getAwsCredentials()))
                .withRegion(Regions.US_EAST_1)
                .build();
        try {
            return amazonSQS.receiveMessage(receiveMessageRequest).getMessages();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            amazonSQS.shutdown();
        }
    }

    public void deleteMessage(Message message) {
        Try.run(() -> {
            var amazonSQS = AmazonSQSClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(webTierProperties.getAwsCredentials()))
                    .withRegion(Regions.US_EAST_1)
                    .build();
            amazonSQS.deleteMessage(webTierProperties.getResponseQueue(), message.getReceiptHandle());
            log.info("Deleting Message from the Response Queue");
            amazonSQS.shutdown();
        }).onFailure(ex -> log.error(ex.getMessage()));
    }
}
