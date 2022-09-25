package com.cc.app.properties;

import com.amazonaws.auth.BasicAWSCredentials;
import lombok.Getter;

@Getter
public class AppTierProperties {

    private final String REQUEST_SQS = "https://sqs.us-east-1.amazonaws.com/507560498503/iaas-request-sqs";
    private final String RESPONSE_SQS = "https://sqs.us-east-1.amazonaws.com/507560498503/iaas-response-sqs";

    private final String REQUEST_S3 = "iaas-546-request-bucket";
    private final String RESPONSE_S3 = "iaas-546-response-bucket";

    public BasicAWSCredentials awsCredentials() {
        return new BasicAWSCredentials(
                "",
                ""
        );
    }
}