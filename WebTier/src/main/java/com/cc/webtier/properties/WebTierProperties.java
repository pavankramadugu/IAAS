package com.cc.webtier.properties;

import com.amazonaws.auth.BasicAWSCredentials;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class WebTierProperties {

    private final String requestQueue;

    private final String responseQueue;

    private final String requestS3Bucket;

    private final String responseS3Bucket;

    private final String appTierImageId;

    private final String awsKeyPair;

    private final String awsAccessKey;

    private final String awsSecretKey;

    private final String securityGroup;

    private final Integer maxInstances;

    public WebTierProperties(@Value("${sqs.queue.request}") String requestQueue,
                             @Value("${sqs.queue.response}") String responseQueue,
                             @Value("${s3.bucket.request}") String requestS3Bucket,
                             @Value("${s3.bucket.response}") String responseS3Bucket,
                             @Value("${ami.id}") String appTierImageId,
                             @Value("${login.keypair}") String awsKeyPair,
                             @Value("${login.access}") String awsAccessKey,
                             @Value("${login.secret}") String awsSecretKey,
                             @Value("${login.sg}") String securityGroup,
                             @Value("${ec2.max-instances}") Integer maxInstances) {
        this.requestQueue = requestQueue;
        this.responseQueue = responseQueue;
        this.requestS3Bucket = requestS3Bucket;
        this.responseS3Bucket = responseS3Bucket;
        this.appTierImageId = appTierImageId;
        this.awsKeyPair = awsKeyPair;
        this.awsAccessKey = awsAccessKey;
        this.awsSecretKey = awsSecretKey;
        this.securityGroup = securityGroup;
        this.maxInstances = maxInstances;
    }

    public BasicAWSCredentials getAwsCredentials() {
        return new BasicAWSCredentials(
                awsAccessKey,
                awsSecretKey
        );
    }
}
