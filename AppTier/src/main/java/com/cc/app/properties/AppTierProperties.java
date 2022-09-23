package com.cc.app.properties;

import com.amazonaws.auth.BasicAWSCredentials;
import lombok.Getter;

@Getter
public class AppTierProperties {

    private final String REQUEST_SQS = "https://sqs.us-east-1.amazonaws.com/620247385745/iaas-request-sqs";
    private final String RESPONSE_SQS = "https://sqs.us-east-1.amazonaws.com/620247385745/iaas-response-sqs";

    private final String REQUEST_S3 = "iaas-request-bucket";
    private final String RESPONSE_S3 = "iaas-response-bucket";

    public BasicAWSCredentials awsCredentials() {
        return new BasicAWSCredentials(
                "",
                ""
        );
    }
}